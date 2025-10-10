package com.shareday.auth.repository;

import com.shareday.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자 조회 (필요하면 추가)
    Optional<User> findByNickname(String nickname);

    // 이메일 존재 여부만 확인 (중복 체크 시 유용)
    boolean existsByEmail(String email);