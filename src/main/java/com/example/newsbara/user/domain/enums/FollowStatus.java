package com.example.newsbara.user.domain.enums;

public enum FollowStatus {
    PENDING,    // 대기
    ACCEPTED,   // 수락
    REJECTED    // 거절 (취소 시 삭제하지 않고 상태 변경)
}
