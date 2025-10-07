package com.shareday.user.repository;

import com.shareday.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);

    // ✅ 이메일로 User 찾기
    Optional<User> findByEmail(String email);

}
