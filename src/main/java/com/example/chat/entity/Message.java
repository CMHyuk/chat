package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Message extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    private String senderName;
    private String receiverName;
    private String message;

    // 1.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public Message(String senderName, String receiverName, String message, Room room) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.message = message;
        this.room = room;
    }
}
