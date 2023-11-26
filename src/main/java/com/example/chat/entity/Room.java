package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String roomId;          // uuid

    private String roomName;

    private String senderName;			// 채팅방 생성자 이름

    private String receiverName;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)			// 채팅방과 Store는 N:1 연관관계
    @JoinColumn(name = "store_id")
    private Store store;

    @Builder
    public Room(String roomId, String roomName, String senderName, String receiverName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

}
