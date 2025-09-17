package com.shareday.couple.service;

import com.shareday.couple.dto.CoupleRequest;
import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;

    // 간단히 메모리 맵으로 초대 코드 저장 (실제 운영에서는 Redis, DB 사용 권장)
    private final Map<String, Long> inviteCodeStore = new HashMap<>();

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

    public InviteResponse generateInviteCode() {
        String code = UUID.randomUUID().toString().substring(0, 8);
        // 여기서는 단순히 ID 0과 매핑 (실제 구현은 로그인 사용자 ID와 매핑 필요)
        inviteCodeStore.put(code, 0L);
        return new InviteResponse(code);
    }

    public CoupleResponse acceptInvite(InviteAcceptRequest request) {
        Long inviterId = inviteCodeStore.get(request.inviteCode());
        if (inviterId == null) {
            throw new IllegalArgumentException("Invalid invite code");
        }

        Couple couple = Couple.builder()
                .startDate(null) // 교제 시작일은 이후 수정 가능
                .build();
        Couple saved = coupleRepository.save(couple);

        // 사용된 코드 삭제
        inviteCodeStore.remove(request.inviteCode());

        return new CoupleResponse(saved.getCoupleId(), saved.getStartDate(), saved.getCreatedAt(), saved.getUpdatedAt());
    }
}
