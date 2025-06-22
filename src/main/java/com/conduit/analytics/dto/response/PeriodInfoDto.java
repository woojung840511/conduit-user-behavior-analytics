package com.conduit.analytics.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PeriodInfoDto {

    @Getter
    @RequiredArgsConstructor
    public enum PeriodType {
        YESTERDAY("어제"),
        RECENT_WEEK("최근 일주일(최근 일요일부터 토요일까지)"),
        RECENT_SIX_MONTHS("최근 6개월");

        public final String description;
    }

    private String periodType;  // 기간 유형 (어제, 최근 일주일, 최근 6개월)
    private String description;  // 기간 설명
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public static PeriodInfoDto from(PeriodType periodType) {
        return switch (periodType) {
            case YESTERDAY -> {
                LocalDate yesterday = LocalDate.now().minusDays(1);
                yield new PeriodInfoDto(
                    periodType.name(),
                    periodType.description,
                    yesterday.atStartOfDay(),
                    yesterday.atTime(23, 59, 59)
                );
            }
            case RECENT_WEEK -> { // 최근 일주일(최근 일요일부터 토요일까지)
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime lastSunday = now.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime lastSaturday = lastSunday.plusDays(6)
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

                yield new PeriodInfoDto(
                    PeriodType.RECENT_WEEK.name(),
                    PeriodType.RECENT_WEEK.description,
                    lastSunday,
                    lastSaturday
                );
            }
            case RECENT_SIX_MONTHS -> {
                LocalDateTime endDate = LocalDateTime.now();
                LocalDateTime startDate = endDate.minusMonths(6);

                yield new PeriodInfoDto(
                    PeriodType.RECENT_SIX_MONTHS.name(),
                    PeriodType.RECENT_SIX_MONTHS.description,
                    startDate,
                    endDate
                );
            }
        };
    }


    public static PeriodInfoDto from(LocalDateTime startDate, LocalDateTime endDate) {
        return new PeriodInfoDto(
            "CUSTOM",
            "사용자 지정 기간",
            startDate,
            endDate
        );
    }

}
