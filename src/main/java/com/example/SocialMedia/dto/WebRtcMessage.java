package com.example.SocialMedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebRtcMessage {
    // Loại tín hiệu: "OFFER", "ANSWER", "ICE_CANDIDATE", "REJECT", "HANGUP"
    private String type;

    // Username người gửi (để người nhận biết ai đang gọi)
    private String sender;

    // Username người nhận (để Server biết chuyển tin nhắn cho ai)
    private String receiver;

    // Dữ liệu WebRTC (SDP hoặc ICE Candidate)
    // Để Object để linh hoạt:
    // - Nếu type là OFFER/ANSWER: data là { sdp: "...", type: "offer" }
    // - Nếu type là ICE_CANDIDATE: data là { candidate: "...", sdpMid: "...", ... }
    private Object data;
}