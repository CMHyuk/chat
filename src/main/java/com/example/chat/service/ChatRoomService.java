package com.example.chat.service;

import com.example.chat.dto.ChatRoomRequestDto;
import com.example.chat.dto.ChatRoomResponseDto;
import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /** ChatRoom 조회 */
    @Transactional
    public ChatRoomResponseDto findById(final Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatRoom이 존재하지 않습니다. id = " + id));
        return new ChatRoomResponseDto(entity);
    }

    /** ChatRoom 생성 */
    @Transactional
    public Long save(final ChatRoomRequestDto requestDto) {
        return chatRoomRepository.save(requestDto.toEntity()).getId();
    }

    /** ChatRoom 수정 */
    @Transactional
    public Long update(final Long id, ChatRoomRequestDto requestDto) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatRoom이 존재하지 않습니다. id = " + id));
        return entity.update(requestDto);
    }

    /** ChatRoom 삭제 */
    public void delete(final Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 ChatRoom이 존재하지 않습니다. id = " + id));
        this.chatRoomRepository.delete(entity);
    }

}
