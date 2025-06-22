package com.conduit.analytics.dto.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyStatDto {

    private Integer dayOfWeek;      // 1 (월요일) ~ 7 (일요일)
    private String actionType;      // VIEW, CLICK, VOTE 등
    private Integer totalActionCount;  // 해당 요일의 총 행동 횟수
    private Long uniqueUserCount;   // 해당 요일에 행동한 고유 사용자 수

    public String getDayOfWeekName() {
        return switch (dayOfWeek) {
            case 1 -> "월요일";
            case 2 -> "화요일";
            case 3 -> "수요일";
            case 4 -> "목요일";
            case 5 -> "금요일";
            case 6 -> "토요일";
            case 7 -> "일요일";
            default -> "알 수 없음";
        };
    }
}
