package com.example.SocialMedia.service.social;

import com.example.SocialMedia.model.coredata_model.InteractableItems;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface InteractableItemService {
    InteractableItems createInteractableItems(String type, LocalDateTime createdAt); //'POST', 'MEDIA', 'COMMENT', 'SHARE', 'MESSAGE', 'STORY'
}
