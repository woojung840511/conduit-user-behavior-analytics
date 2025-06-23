package com.conduit.analytics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_last_action", indexes = {
    @Index(name = "idx_last_action_time", columnList = "last_action_time")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLastAction {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "last_action_time", nullable = false)
    private LocalDateTime lastActionTime;

    @Builder
    public UserLastAction(Long userId, LocalDateTime lastActionTime) {
        this.userId = userId;
        this.lastActionTime = lastActionTime != null ? lastActionTime : LocalDateTime.now();
    }

    // 편의 메서드들
    public static UserLastAction create(Long userId) {
        return UserLastAction.builder()
            .userId(userId)
            .lastActionTime(LocalDateTime.now())
            .build();
    }

    public static UserLastAction create(Long userId, LocalDateTime actionTime) {
        return UserLastAction.builder()
            .userId(userId)
            .lastActionTime(actionTime)
            .build();
    }

    // 마지막 활동 시간 업데이트
    public void updateLastActionTime(LocalDateTime actionTime) {
        this.lastActionTime = actionTime != null ? actionTime : LocalDateTime.now();
    }

    // 특정 기간 내 활성 사용자인지 확인
    public boolean isActiveWithin(int months) {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(months);
        return this.lastActionTime.isAfter(threshold);
    }
}