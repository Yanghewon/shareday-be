package com.shareday.dday.repository;

import com.shareday.dday.entity.Dday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DdayRepository extends JpaRepository<Dday, Long> {
    List<Dday> findAllByUserId(Long userId);
}
