package com.conduit.analytics.dto.repository;

import com.conduit.analytics.enums.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourStatDto {

    private TimePeriod timePeriod;  // 시간대 (새벽, 오전, 오후, 야간)
    private String actionType;      // 행동 유형 (VIEW, CLICK, VOTE 등)
    private Integer totalActionCount;  // 해당 시간대의 총 행동 횟수
    private Long uniqueUserCount;   // 해당 시간대에 행동한 고유 사용자 수


}
