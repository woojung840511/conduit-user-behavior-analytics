package com.conduit.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourlyStatsResponseDto {

    private AnalyticsResultDto yesterday;           // 어제의 시간대별 통계(총합)
    private AnalyticsResultDto sixMonthsAverage;    // 최근 6개월 평균 시간대별 통계(평균)

}
