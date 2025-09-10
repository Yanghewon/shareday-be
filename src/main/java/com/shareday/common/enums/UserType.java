package com.shareday.common.enums;

public enum UserType {
    OWNER,     // 캘린더 소유자
    EDITOR,    // 일정 추가/수정 가능
    VIEWER,    // 읽기 전용 (보기만 가능)
    GUEST      // 초대받은 외부 사용자 (권한 제한적)
}
