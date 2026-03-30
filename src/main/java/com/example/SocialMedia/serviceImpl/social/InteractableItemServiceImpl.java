package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.constant.TargetType;
import com.example.SocialMedia.model.coredata_model.InteractableItems;
import com.example.SocialMedia.repository.InteractableItemRepository;
import com.example.SocialMedia.service.social.InteractableItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InteractableItemServiceImpl implements InteractableItemService {
    private final InteractableItemRepository interactableItemsRepository;

    @Autowired
    public InteractableItemServiceImpl(InteractableItemRepository interactableItemsRepository) {
        this.interactableItemsRepository = interactableItemsRepository;
    }

    @Override
    public InteractableItems createInteractableItems(String type, LocalDateTime createdAt) {
        InteractableItems item = new InteractableItems();
        item.setItemType(TargetType.POST);
        item.setCreatedAt(createdAt);
        return interactableItemsRepository.save(item);
    }
}
