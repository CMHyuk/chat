package com.example.chat.repository;

import com.example.chat.entity.Room;
import com.example.chat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByUserOrReceiverName(User user, String receiverName);

    Optional<Room> findByIdAndUserOrIdAndReceiverName(Long id1, User user, Long id2, String nickname);

    Room findBySenderNameAndReceiverName(String senderName, String receiverName);

    Optional<Room> findByRoomIdAndUserOrRoomIdAndReceiverName(String roomId1, User user, String roomId2, String nickname);

    Optional<Room> findByRoomId(String roomId);
}
