package com.example.SocialMedia.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocketResponse<T> {
    private String type; // Enum: "NEW_MESSAGE", "READ_RECEIPT", "TYPING"
    private T payload;
}