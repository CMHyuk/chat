package com.example.chat.service;

import com.example.chat.dto.MessageRequestDto;
import com.example.chat.dto.MessageResponseDto;
import com.example.chat.dto.RoomDto;
import com.example.chat.entity.Room;
import com.example.chat.entity.Store;
import com.example.chat.entity.User;
import com.example.chat.repository.RoomRepository;
import com.example.chat.repository.StoreRepository;
import com.example.chat.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    // 채팅방(=topic)에 발행되는 메시지 처리하는 리스너
    private final RedisMessageListenerContainer redisMessageListener;

    // 구독(sub) 처리를 위한 서비스
    private final RedisSubscriber redisSubscriber;

    // 1. redis key = 'MESSAGE_ROOM'
    private static final String Message_Rooms = "MESSAGE_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, RoomDto> opsHashMessageRoom;

    // 2. 채팅방의 대화 메시지 발행을 위한 redis topic(채팅방) 정보
    private Map<String, ChannelTopic> topics;

    // 3. redis 의 Hash 데이터에 접근하기 위한 HashOperations, topics 초기화.
    @PostConstruct
    private void init() {
        opsHashMessageRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    // 채팅방 생성
    @Transactional
    public MessageResponseDto createRoom(MessageRequestDto dto, String email) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow();

//        User receiver = userRepository.findById(dto.getReceiverId()).orElseThrow(()->new InvalidEmailException("수신 회원정보가 존재하지 않습니다."));
        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow();

        User receiver = store.getUser();

        // 4. 다른 사람들은 들어올 수 없도록 1:1 (구매자:판매자) 채팅방 구성하기
        Room messageRoom = roomRepository.findBySenderNameAndReceiverName(sender.getName(), receiver.getName());
        // 5. room 생성하기 - (이미 생성된 채팅방이 아닌 경우)
        if (messageRoom == null) {
            RoomDto roomDto = RoomDto.create(receiver.getName(), sender);
            opsHashMessageRoom.put(Message_Rooms, roomDto.getRoomId(), roomDto);      // redis hash 에 채팅방 저장해서, 서버간 채팅방 공유 가능
            messageRoom = roomRepository.save(new Room(roomDto.getRoomId(), roomDto.getRoomName(), roomDto.getSenderName(), roomDto.getReceiverName()));

            return new MessageResponseDto(messageRoom);
            // 6. 이미 생성된 채팅방인 경우 기존 채팅방 이동
        } else {
            return new MessageResponseDto(messageRoom);
        }
    }

    @Transactional
    public RoomDto findRoom(String roomId, String email) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow();
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        User receiver = userRepository.findByEmail(email)
                .orElseThrow();
        Store store = storeRepository.findById(room.getStore().getId())
                .orElseThrow();

        // 9. sender & receiver 모두 messageRoom 조회 가능
        Room findRoom = roomRepository.findByRoomIdAndUserOrRoomIdAndReceiverName(roomId, user, roomId, room.getReceiverName())
                .orElseThrow();

        return new RoomDto(
                findRoom.getId(),
                findRoom.getRoomName(),
                findRoom.getRoomId(),
                findRoom.getSenderName(),
                findRoom.getReceiverName()
        );
    }

    // 채팅방 입장
    public void enterMessageRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);

        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);        // pub/sub 통신을 위해 리스너를 설정. 대화가 가능해진다
            topics.put(roomId, topic);
        }
    }

    // redis 채널에서 채팅방 조회
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

}
