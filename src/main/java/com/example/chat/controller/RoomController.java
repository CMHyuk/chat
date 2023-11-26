package com.example.chat.controller;

import com.example.chat.dto.MessageRequestDto;
import com.example.chat.dto.MessageResponseDto;
import com.example.chat.dto.RoomDto;
import com.example.chat.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    // 채팅방 생성
    @PostMapping("/room")
    public MessageResponseDto createRoom(@RequestBody MessageRequestDto messageRequestDto, String email) {
        return roomService.createRoom(messageRequestDto, email);
    }

    // 사용자 관련 채팅방 선택 조회
    @GetMapping("/room/{roomId}")
    public RoomDto findRoom(@PathVariable String roomId, String email) {
        return roomService.findRoom(roomId, email);
    }

}
