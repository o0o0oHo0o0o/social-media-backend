package com.example.SocialMedia.repository;

import com.example.SocialMedia.model.coredata_model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    List<RefreshToken> findByUserId(int userId);
    Optional<RefreshToken> findByToken(String refreshToken);
}
