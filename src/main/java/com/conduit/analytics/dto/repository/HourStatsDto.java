package com.conduit.analytics.dto.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourStatsDto {

    private String timePeriod;      // 시간대 (새벽, 오전, 오후, 야간)
    private String actionType;      // 행동 유형 (VIEW, CLICK, VOTE 등)
    private Long totalActionCount;  // 해당 시간대의 총 행동 횟수
    private Long uniqueUserCount;   // 해당 시간대에 행동한 고유 사용자 수

    public int getTimePeriodOrder() {
        return switch (timePeriod) {
            case "새벽" -> 1;
            case "오전" -> 2;
            case "오후" -> 3;
            case "야간" -> 4;
            default -> 99;
        };
    }
}
