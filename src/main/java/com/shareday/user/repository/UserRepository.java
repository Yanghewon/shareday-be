package com.shareday.user.repository;

import com.shareday.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 필요하다면 커스텀 메서드 추가 가능
    // Optional<User> findByEmail(String email);
=======
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByKakaoId(String kakaoId); // ✅ 추가
>>>>>>> 6a996ea (feat: 사용자 정보 조회 API 추가 및 OAuth2 인증 로직 개선)
}
