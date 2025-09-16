package com.shareday.couple.service;

import com.shareday.couple.dto.CoupleRequest;
import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;

    public List<CoupleResponse> findAll() {
        return coupleRepository.findAll().stream()
                .map(c -> new CoupleResponse(c.getCoupleId(), c.getStartDate(), c.getCreatedAt(), c.getUpdatedAt()))
                .toList();
    }

    public CoupleResponse findById(Long id) {
        Couple couple = coupleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Couple not found"));
        return new CoupleResponse(couple.getCoupleId(), couple.getStartDate(), couple.getCreatedAt(), couple.getUpdatedAt());
    }

    public CoupleResponse create(CoupleRequest request) {
        Couple couple = Couple.builder()
                .startDate(request.startDate())
                .build();
        Couple saved = coupleRepository.save(couple);
        return new CoupleResponse(saved.getCoupleId(), saved.getStartDate(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    public CoupleResponse update(Long id, CoupleRequest request) {
        Couple couple = coupleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Couple not found"));
        couple.setStartDate(request.startDate());
        Couple updated = coupleRepository.save(couple);
        return new CoupleResponse(updated.getCoupleId(), updated.getStartDate(), updated.getCreatedAt(), updated.getUpdatedAt());
    }

    public void delete(Long id) {
        coupleRepository.deleteById(id);
    }
}
