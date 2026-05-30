package com.app.oauth.repository;

import com.app.oauth.model.RefreshToken;
import com.app.oauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime now);
}
