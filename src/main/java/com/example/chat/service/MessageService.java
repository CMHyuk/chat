package com.example.chat.service;

import com.example.chat.dto.MessageDto;
import com.example.chat.entity.Message;
import com.example.chat.entity.Room;
import com.example.chat.repository.MessageRepository;
import com.example.chat.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final RedisTemplate<String, MessageDto> redisTemplateMessage;
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    // 메세지 저장
    @Transactional
    public void saveMessage(MessageDto messageDto) {
        Room room = roomRepository.findByRoomId(messageDto.getRoomId())
                .orElseThrow();

        // 1. 직렬화
        redisTemplateMessage.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        // 2. redis 저장
        redisTemplateMessage.opsForList().rightPush(messageDto.getRoomId(), messageDto);

        // 3. redistemplate의 expire() 을 이용해서, Key 를 만료시킬 수 있음
        redisTemplateMessage.expire(messageDto.getRoomId(), 60, TimeUnit.MINUTES);
    }

    // 6. 대화 조회 - Redis & DB (TLB 캐시전략 유사)
    @Transactional
    public List<MessageDto> loadMessage(String roomId) {
        List<MessageDto> messageList = new ArrayList<>();

        // Redis 에서 해당 채팅방의 메시지 100개 가져오기
        List<MessageDto> redisMessageList = redisTemplateMessage.opsForList().range(roomId, 0, 99);
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow();

        // 4. cache miss - Redis 에서 가져온 메시지가 없다면, DB 에서 메시지 100개 가져오기
        if (redisMessageList == null || redisMessageList.isEmpty()) {
            // 5. 생성시간이 빠른 순서부터 100개의 데이터 정렬
            List<Message> dbMessageList = messageRepository.findTop100ByRoomOrderById(room);      // db 저장된 채팅내역

            // db 저장된 내역을 redis에 저장
            for (Message message : dbMessageList) {
                MessageDto messageDto = new MessageDto(message);
                messageList.add(messageDto);
                redisTemplateMessage.setValueSerializer(new Jackson2JsonRedisSerializer<>(Message.class));      // 직렬화 이후
                redisTemplateMessage.opsForList().rightPush(roomId, messageDto);                                // redis 저장
            }
        } else { // cache hit
            // 7. 최근 내역 100개 저장
            messageList.addAll(redisMessageList);
        }

        return messageList;
    }
}