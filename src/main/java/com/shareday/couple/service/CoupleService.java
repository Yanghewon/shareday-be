package com.shareday.couple.service;

import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleService {

    private final CoupleRepository coupleRepository;

    // 초대 코드 생성 (Couple 엔티티 생성만, 아직 accepted = false)
    public InviteResponse generateInviteCode() {
        String code = UUID.randomUUID().toString().substring(0, 8);

        Couple couple = Couple.builder()
                .inviteCode(code)
                .startDate(null) // 수락 시점에 결정
                .build();

        coupleRepository.save(couple);

        return new InviteResponse(code);
    }

    // 초대 코드 수락 (accepted = true, 커플 확정)
    public CoupleResponse acceptInvite(InviteAcceptRequest request) {
        Couple couple = coupleRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite code"));

        if (couple.isAccepted()) {
            throw new IllegalStateException("Already accepted invite code");
        }

        couple.setAccepted(true);
        couple.setStartDate(LocalDate.now());
        Couple saved = coupleRepository.save(couple);

        return new CoupleResponse(saved.getCoupleId(), saved.getStartDate(), saved.getInviteCode());
    }

    public void delete(Long id) {
        coupleRepository.deleteById(id);
    }
}
