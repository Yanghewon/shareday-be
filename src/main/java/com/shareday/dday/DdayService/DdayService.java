package com.shareday.dday.service;

import com.shareday.dday.dto.DdayRequest;
import com.shareday.dday.dto.DdayResponse;
import com.shareday.dday.entity.Dday;
import com.shareday.dday.repository.DdayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DdayService {

    private final DdayRepository ddayRepository;

    public List<DdayResponse> getAll(Long userId) {
        return ddayRepository.findAllByUserId(userId)
                .stream()
                .map(d -> new DdayResponse(d.getId(), d.getTitle(), d.getTargetDate(), d.isDday(), d.getMemo()))
                .toList();
    }

    public DdayResponse getOne(Long ddayId, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));
        return new DdayResponse(dday.getId(), dday.getTitle(), dday.getTargetDate(), dday.isDday(), dday.getMemo());
    }

    public DdayResponse create(DdayRequest request, Long userId) {
        Dday dday = Dday.builder()
                .title(request.title())
                .targetDate(request.targetDate())
                .memo(request.memo())
                .isDday(true)
                .userId(userId)
                .build();
        Dday saved = ddayRepository.save(dday);
        return new DdayResponse(saved.getId(), saved.getTitle(), saved.getTargetDate(), saved.isDday(), saved.getMemo());
    }

    public DdayResponse update(Long ddayId, DdayRequest request, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));

        dday.setTitle(request.title());
        dday.setTargetDate(request.targetDate());
        dday.setMemo(request.memo());
        return new DdayResponse(dday.getId(), dday.getTitle(), dday.getTargetDate(), dday.isDday(), dday.getMemo());
    }

    public void delete(Long ddayId, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));
        ddayRepository.delete(dday);
    }
}
