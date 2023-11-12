package com.example.chat.controller;

import com.example.chat.dto.ChatRoomRequestDto;
import com.example.chat.dto.ChatRoomResponseDto;
import com.example.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public Long createRoom(@RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        return chatRoomService.save(chatRoomRequestDto);
    }

    @GetMapping("/room/{roomId}")
    public ChatRoomResponseDto roomInfo(@PathVariable Long roomId) {
        return chatRoomService.findById(roomId);
    }

}
