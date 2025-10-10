package com.shareday.couple.repository;

import com.shareday.couple.entity.CoupleInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoupleInviteRepository extends JpaRepository<CoupleInvite, Long> {
    Optional<CoupleInvite> findByCode(String code);
}
