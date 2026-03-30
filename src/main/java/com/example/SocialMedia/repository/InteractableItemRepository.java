package com.example.SocialMedia.repository;

import com.example.SocialMedia.model.coredata_model.InteractableItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractableItemRepository extends JpaRepository<InteractableItems, Integer> {

}
