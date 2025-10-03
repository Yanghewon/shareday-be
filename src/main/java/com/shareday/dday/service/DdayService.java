package com.shareday.dday.service;

import com.shareday.dday.dto.DdayRequest;
import com.shareday.dday.dto.DdayResponse;
import com.shareday.dday.entity.Dday;
import com.shareday.dday.repository.DdayRepository;
import com.shareday.event.entity.Event;
import com.shareday.event.enums.ParticipantType;
import com.shareday.event.repository.EventRepository;
import com.shareday.user.entity.User;
import com.shareday.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DdayService {

    private final DdayRepository ddayRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<DdayResponse> getAll(Long userId) {
        return ddayRepository.findAllByUserId(userId)
                .stream()
                .map(d -> new DdayResponse(d.getId(), d.getTitle(), d.getTargetDate()))
                .toList();
    }

    public DdayResponse getOne(Long ddayId, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));
        return new DdayResponse(dday.getId(), dday.getTitle(), dday.getTargetDate());
    }

    public DdayResponse create(DdayRequest request, Long userId) {
        // 1. Dday 저장
        Dday dday = Dday.builder()
                .title(request.title())
                .targetDate(request.targetDate())
                .userId(userId)
                .build();
        Dday saved = ddayRepository.save(dday);

        // 2. user → coupleId 조회
        Long coupleId = userRepository.findById(userId)
                .map(user -> user.getCouple() != null ? user.getCouple().getCoupleId() : 0L)
                .orElse(0L);

        // 3. Event 저장
        Event event = Event.builder()
                .coupleId(coupleId)
                .userId(userId)
                .title(request.title())
                .description(request.title()) // ✅ description 중복 방지: title만
                .eventDate(request.targetDate())
                .startTime(request.targetDate().atStartOfDay())
                .endTime(request.targetDate().atTime(23, 59, 59))
                .participantType(ParticipantType.PARTNER)
                .isDday(true)
                .build();

        eventRepository.save(event);

        return new DdayResponse(saved.getId(), saved.getTitle(), saved.getTargetDate());
    }

    public DdayResponse update(Long ddayId, DdayRequest request, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));

        dday.setTitle(request.title());
        dday.setTargetDate(request.targetDate());
        return new DdayResponse(dday.getId(), dday.getTitle(), dday.getTargetDate());
    }

    public void delete(Long ddayId, Long userId) {
        Dday dday = ddayRepository.findById(ddayId)
                .filter(d -> d.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("D-Day를 찾을 수 없습니다."));
        ddayRepository.delete(dday);
    }
}
