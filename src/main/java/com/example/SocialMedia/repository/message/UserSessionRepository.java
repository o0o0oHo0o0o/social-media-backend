package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.coredata_model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    @Query("SELECT ws FROM UserSession ws " +
            "WHERE ws.sessionToken = :token " +
            "AND ws.isActive = true " +
            "AND ws.expiresAt > :now")
    Optional<UserSession> findValidSessionByToken(@Param("token") String token, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserSession ws SET ws.isActive = false WHERE ws.expiresAt < :now AND ws.isActive = true")
    int deactivateExpiredSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE UserSession ws SET ws.isActive = false WHERE ws.user.id = :userId AND ws.isActive = true")
    void deactivateUserSessions(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE UserSession ws SET ws.isActive = false WHERE ws.sessionToken = :token AND ws.isActive = true")
    int deactivateByToken(@Param("token") String token);
}