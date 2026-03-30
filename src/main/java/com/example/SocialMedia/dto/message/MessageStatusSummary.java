package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusSummary {
    private int sentCount;
    private int deliveredCount;
    private int readCount;
    private List<UserReadStatus> readByUsers;
}
