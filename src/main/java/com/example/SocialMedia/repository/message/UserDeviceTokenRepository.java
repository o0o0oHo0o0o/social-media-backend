package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.coredata_model.UserTokenDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserTokenDevice, Long> {

    Optional<UserTokenDevice> findByDeviceToken(String deviceToken);

    List<UserTokenDevice> findByUser_IdAndIsActive(Integer userId, boolean isActive);

    @Transactional
    @Modifying
    @Query("UPDATE UserTokenDevice udt SET udt.isActive = false WHERE udt.deviceToken = :deviceToken")
    void deactivateToken(@Param("deviceToken") String deviceToken);

    @Transactional
    @Modifying
    @Query("UPDATE UserTokenDevice udt SET udt.lastUsedAt = :now WHERE udt.tokenId = :tokenId")
    void updateLastUsedAt(@Param("tokenId") Long tokenId, @Param("now") LocalDateTime now);
}
