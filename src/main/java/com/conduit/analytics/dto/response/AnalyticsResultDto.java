package com.conduit.analytics.dto.response;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsResultDto {

    private PeriodInfoDto periodInfo;   // 조회 기간 정보

    private Map<String, Map<String, StatisticsDto>> results;

}
