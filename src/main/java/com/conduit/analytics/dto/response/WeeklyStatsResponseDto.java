package com.conduit.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
    public class WeeklyStatsResponseDto { // 요일별, 시간대별 통계 응답 DTO

    private AnalyticsResultDto recentWeek;
    private AnalyticsResultDto sixMonthsAverage;

}
