package com.shareday.couple.repository;

import com.shareday.couple.entity.Couple;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
}
