package com.shareday.couple.service;

import com.shareday.couple.dto.CoupleResponse;
import com.shareday.couple.dto.InviteAcceptRequest;
import com.shareday.couple.dto.InviteResponse;
import com.shareday.couple.entity.Couple;
import com.shareday.couple.repository.CoupleRepository;
import com.shareday.user.entity.User;
import com.shareday.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final UserRepository userRepository;

    /**
     * 초대 코드 생성
     * - 로그인한 사용자가 초대자(inviter)
     * - Couple 생성 후 초대자를 Couple에 연결
     */
    // CoupleService.generateInviteCode 수정
    public InviteResponse generateInviteCode(Long inviterUserId) {
        User inviter = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Couple existing = inviter.getCouple();

        if (existing != null) {
            // 이미 커플에 연결되어 있음
            if (!existing.isAccepted()) {
                // 수락 전이면 기존 코드 그대로 반환 (UX 개선)
                String code = existing.getInviteCode();
                if (code == null || code.isBlank()) {
                    // 혹시 코드가 없으면 새로 발급
                    code = UUID.randomUUID().toString().substring(0, 8);
                    existing.setInviteCode(code);
                    coupleRepository.save(existing);
                }
                return new InviteResponse(code);
            }
            // 수락 완료 상태면 새 초대 불가
            throw new IllegalStateException("이미 커플 상태입니다.");
        }

        // 아직 커플 아님 → 새 커플 생성 + 초대자 연결
        String code = UUID.randomUUID().toString().substring(0, 8);
        Couple couple = Couple.builder()
                .inviteCode(code)
                .accepted(false)
                .build();
        Couple saved = coupleRepository.save(couple);

        inviter.setCouple(saved);
        userRepository.save(inviter);

        return new InviteResponse(code);
    }

    /**
     * 초대 코드 수락
     * - 코드로 Couple 조회
     * - 수락자를 같은 Couple에 연결
     * - Couple.accepted = true, startDate = 오늘
     */
    public CoupleResponse acceptInvite(InviteAcceptRequest request, Long acceptorUserId) {
        Couple couple = coupleRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        if (couple.isAccepted()) {
            throw new IllegalStateException("이미 수락된 초대 코드입니다.");
        }

        User acceptor = userRepository.findById(acceptorUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (acceptor.getCouple() != null) {
            throw new IllegalStateException("이미 커플 상태입니다.");
        }

        // ✅ 수락자-커플 연결
        acceptor.setCouple(couple);
        userRepository.save(acceptor);

        // ✅ 커플 확정
        couple.setAccepted(true);
        couple.setStartDate(LocalDate.now());
        Couple saved = coupleRepository.save(couple);

        return new CoupleResponse(saved.getCoupleId(), saved.getStartDate(), saved.getInviteCode());
    }

    /**
     * 커플 해제 (양쪽 유저 연결 해제 후 Couple 삭제)
     */
    public void delete(Long coupleId) {
        // ✅ 이 Couple에 속한 모든 유저 연결 해제
        List<User> users = userRepository.findByCouple_CoupleId(coupleId);
        for (User user : users) {
            user.setCouple(null);
        }
        userRepository.saveAll(users);

        // ✅ 커플 삭제
        coupleRepository.deleteById(coupleId);
    }
}
