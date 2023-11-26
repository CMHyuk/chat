package com.example.chat.repository;

import com.example.chat.entity.Message;
import com.example.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findTop100ByRoomOrderById(Room room);
}

