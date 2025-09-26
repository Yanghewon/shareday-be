package com.shareday.couple.repository;

import com.shareday.couple.entity.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByInviteCode(String inviteCode);
}
