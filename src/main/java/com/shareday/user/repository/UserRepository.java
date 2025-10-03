package com.shareday.user.repository;

import com.shareday.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ PK(userId) 기준 조회
    Optional<User> findByUserId(Long userId);

    // ✅ 카카오 ID로 조회
    Optional<User> findByKakaoId(String kakaoId);

    // ✅ 이메일로 조회
    Optional<User> findByEmail(String email);
}
