package com.shareday.user.repository;

import com.shareday.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // ✅ 특정 Couple에 속한 모든 사용자 조회용
    List<User> findByCouple_CoupleId(Long coupleId);
}
