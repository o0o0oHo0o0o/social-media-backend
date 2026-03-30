package com.example.SocialMedia.repository;

import com.example.SocialMedia.dto.response.ReactionStat;
import com.example.SocialMedia.model.coredata_model.InteractableItems;
import com.example.SocialMedia.model.coredata_model.Reaction;
import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    Optional<Reaction> findByInteractableItemsAndUser_Id(InteractableItems item, Integer userId);
    List<Reaction> findByInteractableItems_InteractableItemIdIn(List<Integer> itemIds);
    @Query("SELECT r.reactionType AS reactionType, COUNT(r) AS reactionCount " +
            "FROM Reaction r " +
            "WHERE r.interactableItems.interactableItemId = :itemId " +
            "GROUP BY r.reactionType")
    List<ReactionStat> countReactionsByInteractableItemId(@Param("itemId") Integer itemId);

    Optional<Reaction> findReactionByInteractableItemsAndUser(InteractableItems interactableItems, User user);

    Optional<Reaction> findByInteractableItems_InteractableItemIdAndUser_Id(int interactableItemId, Integer userId);
}
