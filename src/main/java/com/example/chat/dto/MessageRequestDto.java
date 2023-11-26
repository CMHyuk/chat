package com.example.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown =true)
public class MessageRequestDto {
    private Long storeId;       // 1:1 채팅하기를 시작한 가게 정보
}
