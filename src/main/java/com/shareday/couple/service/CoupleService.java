package com.shareday.couple.service;

import com.shareday.couple.dto.CoupleRequest;
import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;

    public CoupleResponse create(CoupleRequest request) {
        Couple couple = Couple.builder()
                .startDate(request.startDate())
                .build();
        Couple saved = coupleRepository.save(couple);
        return new CoupleResponse(saved.getStartDate());
    }

    public CoupleResponse update(Long id, CoupleRequest request) {
        Couple couple = coupleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Couple not found"));
        couple.setStartDate(request.startDate());
        Couple updated = coupleRepository.save(couple);
        return new CoupleResponse(updated.getStartDate());
    }

    public void delete(Long id) {
        coupleRepository.deleteById(id);
    }

    // 초대 코드 생성 (DB에 영구 저장)
    public InviteResponse generateInviteCode(Long coupleId) {
        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(() -> new IllegalArgumentException("Couple not found"));

        if (couple.getInviteCode() != null) {
            // 이미 코드 있으면 그대로 반환
            return new InviteResponse(couple.getInviteCode());
        }

        String code = UUID.randomUUID().toString().substring(0, 8);
        couple.setInviteCode(code);
        coupleRepository.save(couple);

        return new InviteResponse(code);
    }

    // 초대 코드 수락
    public CoupleResponse acceptInvite(InviteAcceptRequest request) {
        Couple couple = coupleRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        // 필요하다면 수락 로직 추가 (예: 상대방 유저 등록)
        // 여기서는 단순히 교제 시작일 반환
        return new CoupleResponse(couple.getStartDate());
    }
}
