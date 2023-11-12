package com.example.chat.service;

import com.example.chat.dto.ChatMessageRequestDto;
import com.example.chat.dto.ChatMessageResponseDto;
import com.example.chat.entity.ChatMessage;
import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatMessageRepository;
import com.example.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.transaction.annotation.Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /** ChatMessage 조회 */
    public ChatMessageResponseDto findById(final Long chatMessageId) {
        ChatMessage chatMessageEntity = chatMessageRepository.findById(chatMessageId).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatMessage가 존재하지 않습니다. chatMessageId = " + chatMessageId));
        return new ChatMessageResponseDto(chatMessageEntity);
    }

    public List<ChatMessageResponseDto> findByChatRoom(final Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow();
        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom(chatRoom);
        return messages.stream().map(m -> new ChatMessageResponseDto(m))
                .collect(Collectors.toList());
    }


    /** ChatMessage 생성 */
    @Transactional
    public Long save(final Long chatRoomId, final ChatMessageRequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatRoom이 존재하지 않습니다. chatRoomId = " + chatRoomId));
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .message(requestDto.getMessage())
                .sender(requestDto.getSender())
                .build();
        return this.chatMessageRepository.save(chatMessage).getId();
    }

    /** ChatMessage 삭제 */
    @Transactional
    public void delete(final Long chatMessageId) {
        ChatMessage chatMessageEntity = chatMessageRepository.findById(chatMessageId).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatMessage가 존재하지 않습니다. chatMessageId = " + chatMessageId));
        this.chatMessageRepository.delete(chatMessageEntity);
    }

}
