package com.conduit.analytics.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "action_target", length = 100)
    private String actionTarget;

    @Column(name = "action_count", nullable = false)
    private Integer actionCount;

    @Column(name = "action_value")
    private Double actionValue;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;

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