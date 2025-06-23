package com.conduit.analytics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_history", indexes = {
    @Index(name = "idx_action_time", columnList = "action_time"),
    @Index(name = "idx_actiontype_time_userid", columnList = "action_type, action_time, user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionHistory { // 사용자 행동 기록 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // 예: VIEW, CLICK, VOTE 등

    @Column(name = "action_target", length = 100)
    private String actionTarget; // 예: vote, product_123 등

    @Column(name = "action_count", nullable = false)
    private Integer actionCount;

    @Column(name = "action_value")
    private Double actionValue;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;   // UTC 기준

    @Builder
    public ActionHistory(Long userId, String actionType, String actionTarget,
        Integer actionCount, Double actionValue, LocalDateTime actionTime) {
        this.userId = userId;
        this.actionType = actionType;
        this.actionTarget = actionTarget;
        this.actionCount = actionCount != null ? actionCount : 1;
        this.actionValue = actionValue;
        this.actionTime = actionTime != null ? actionTime : LocalDateTime.now();
    }

    public static ActionHistory createViewAction(Long userId, String target) {
        return ActionHistory.builder()
            .userId(userId)
            .actionType("VIEW")
            .actionTarget(target)
            .build();
    }

    public static ActionHistory createClickAction(Long userId, String target) {
        return ActionHistory.builder()
            .userId(userId)
            .actionType("CLICK")
            .actionTarget(target)
            .build();
    }

    public static ActionHistory createVoteAction(Long userId, Double value) {
        return ActionHistory.builder()
            .userId(userId)
            .actionType("VOTE")
            .actionTarget("vote")
            .actionValue(value)
            .build();
    }
}