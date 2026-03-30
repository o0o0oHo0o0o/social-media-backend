package com.example.SocialMedia.constant;

public enum MessageType {
    TEXT,           // Tin nhắn văn bản bình thường
    IMAGE,          // Tin nhắn chỉ chứa ảnh
    VIDEO,          // Tin nhắn video
    FILE,           // Tin nhắn đính kèm file (PDF, DOCX...)
    AUDIO,          // Tin nhắn thoại (Voice chat)
    NOTIFICATION,
    // --- CÁC LOẠI ĐẶC BIỆT ---
    SYSTEM,         // Thông báo hệ thống (VD: "A đã đổi tên nhóm")
    CALL_LOG,       // Lưu lịch sử cuộc gọi (VD: "Cuộc gọi thoại - 5 phút")
    STICKER         // Nhãn dán (nếu có)
}