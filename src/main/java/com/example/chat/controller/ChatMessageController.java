package com.example.chat.controller;

import com.example.chat.dto.ChatMessageRequestDto;
import com.example.chat.dto.ChatMessageResponseDto;
import com.example.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/chat/{roomId}/chatting")
    public Long saveMessage(@PathVariable Long roomId, @RequestBody ChatMessageRequestDto chatMessageRequestDto) {
        return chatMessageService.save(roomId, chatMessageRequestDto);
    }

    @GetMapping("/chat/{chatMessageId}")
    public ChatMessageResponseDto getMessage(@PathVariable Long chatMessageId) {
        return chatMessageService.findById(chatMessageId);
    }

    @GetMapping("/chat/all/{chatRoomId}")
    public List<ChatMessageResponseDto> getMessages(@PathVariable Long chatRoomId) {
        return chatMessageService.findByChatRoom(chatRoomId);
    }

}
