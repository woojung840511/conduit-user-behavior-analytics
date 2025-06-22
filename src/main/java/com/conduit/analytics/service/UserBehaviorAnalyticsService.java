package com.conduit.analytics.service;

import com.conduit.analytics.dto.repository.HourStatDto;
import com.conduit.analytics.dto.repository.WeeklyStatDto;
import com.conduit.analytics.dto.response.AnalyticsResultDto;
import com.conduit.analytics.dto.response.FilteredUserCountResponseDto;
import com.conduit.analytics.dto.response.HourlyStatsResponseDto;
import com.conduit.analytics.dto.response.PeriodInfoDto;
import com.conduit.analytics.dto.response.PeriodInfoDto.PeriodType;
import com.conduit.analytics.dto.response.StatisticsDto;
import com.conduit.analytics.dto.response.WeeklyStatsResponseDto;
import com.conduit.analytics.repository.ActionHistoryCustomRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBehaviorAnalyticsService {

    private final ActionHistoryCustomRepository actionHistoryRepository;

    public WeeklyStatsResponseDto getWeeklyStats() {
        log.info("요일별 통계 조회 시작");
        AnalyticsResultDto recentWeekStats = getRecentWeekStats();
        AnalyticsResultDto sixMonthsAverageStats = getSixMonthsWeeklyAverageStats();

        return new WeeklyStatsResponseDto(recentWeekStats, sixMonthsAverageStats);
    }

    public HourlyStatsResponseDto getHourlyStats() {
        log.info("시간대별 통계 조회 시작");

        AnalyticsResultDto yesterdayStats = getYesterdayStats();
        AnalyticsResultDto sixMonthsAverageStats = getSixMonthsHourlyAverageStats();

        return new HourlyStatsResponseDto(yesterdayStats, sixMonthsAverageStats);
    }

    public FilteredUserCountResponseDto getVoteUsersWithRecentActivity() {
        log.info("최근 3개월 내 활동한 사용자 수 조회 시작");
        long userCount = actionHistoryRepository.countVoteUsersWithRecentActivity();

        return new FilteredUserCountResponseDto(
            userCount,
            "최근 6개월간 투표한 사용자 중 최근 3개월 이내 활동한 사용자"
        );
    }

    private AnalyticsResultDto getRecentWeekStats() {

        PeriodInfoDto recentWeek = PeriodInfoDto.from(PeriodType.RECENT_WEEK);

        List<WeeklyStatDto> stats = actionHistoryRepository.getWeeklyActionStats(
            recentWeek.getStartDate(),
            recentWeek.getEndDate()
        );

        Map<String, Map<String, StatisticsDto>> result = convertWeeklyStatsToMap(stats);

        return new AnalyticsResultDto(recentWeek, result);
    }

    private AnalyticsResultDto getSixMonthsWeeklyAverageStats() {
        PeriodInfoDto sixMonths = PeriodInfoDto.from(PeriodType.RECENT_SIX_MONTHS);

        List<WeeklyStatDto> stats = actionHistoryRepository.getWeeklyActionStats(
            sixMonths.getStartDate(),
            sixMonths.getEndDate()
        );

        Map<String, Map<String, StatisticsDto>> totals = convertWeeklyStatsToMap(stats);
        Map<String, Map<String, StatisticsDto>> averages = calculateWeeklyAverages(totals, sixMonths);

        return new AnalyticsResultDto(sixMonths, averages);
    }

    private AnalyticsResultDto getYesterdayStats() {
        PeriodInfoDto yesterday = PeriodInfoDto.from(PeriodType.YESTERDAY);

        List<HourStatDto> stats = actionHistoryRepository.getHourStats(
            yesterday.getStartDate(),
            yesterday.getEndDate()
        );

        Map<String, Map<String, StatisticsDto>> result = convertHourlyStatsToMap(stats);

        return new AnalyticsResultDto(yesterday, result);
    }

    private AnalyticsResultDto getSixMonthsHourlyAverageStats() {
        PeriodInfoDto sixMonths = PeriodInfoDto.from(PeriodType.RECENT_SIX_MONTHS);

        List<HourStatDto> stats = actionHistoryRepository.getHourStats(
            sixMonths.getStartDate(),
            sixMonths.getEndDate()
        );

        Map<String, Map<String, StatisticsDto>> totals = convertHourlyStatsToMap(stats);
        Map<String, Map<String, StatisticsDto>> averages = calculateHourlyAverages(totals, sixMonths);

        return new AnalyticsResultDto(sixMonths, averages);
    }


    private Map<String, Map<String, StatisticsDto>> convertWeeklyStatsToMap(List<WeeklyStatDto> stats) {
        Map<String, Map<String, StatisticsDto>> result = new LinkedHashMap<>();

        for (WeeklyStatDto stat : stats) {
            String dayOfWeek = stat.getDayOfWeekName();

            result.computeIfAbsent(dayOfWeek, k -> new LinkedHashMap<>())
                .put(stat.getActionType(),
                    new StatisticsDto(stat.getTotalActionCount(), stat.getUniqueUserCount()));
        }

        return result;
    }

    private Map<String, Map<String, StatisticsDto>> calculateWeeklyAverages(
        Map<String, Map<String, StatisticsDto>> totals,
        PeriodInfoDto sixMonths
    ) {

        Map<String, Map<String, StatisticsDto>> averages = new LinkedHashMap<>();
        Map<String, Integer> dayCountsInSixMonths = calculateDayOfWeekCounts(sixMonths); // 각 요일이 6개월 동안 몇 번 있었는지 계산

        for (String dayOfWeek : totals.keySet()) {
            Map<String, StatisticsDto> dailyStats = totals.get(dayOfWeek);
            Map<String, StatisticsDto> averageStats = new LinkedHashMap<>();

            Integer dayCounts = dayCountsInSixMonths.get(dayOfWeek);

            for (String actionType : dailyStats.keySet()) {
                StatisticsDto totalStats = dailyStats.get(actionType);
                double averageActions = totalStats.getActionCount() / dayCounts;
                double averageUsers = totalStats.getUniqueUserCount() / dayCounts;

                averageStats.put(actionType, new StatisticsDto(averageActions, averageUsers));
            }

            averages.put(dayOfWeek, averageStats);
        }

        return averages;
    }

    // 6개월 동안 각 요일이 몇 번 있었는지 계산
    private Map<String, Integer> calculateDayOfWeekCounts(PeriodInfoDto sixMonths) {
        Map<String, Integer> dayCountsInSixMonths = new HashMap<>();

        LocalDateTime startDate = sixMonths.getStartDate();
        LocalDateTime endDate = sixMonths.getEndDate();

        LocalDateTime current = startDate;

        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            String dayOfWeek = getDayOfWeekName(current.getDayOfWeek());
            dayCountsInSixMonths.put(dayOfWeek, dayCountsInSixMonths.getOrDefault(dayOfWeek, 0) + 1);
            current = current.plusDays(1);
        }

        return dayCountsInSixMonths;
    }

    private String getDayOfWeekName(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "월요일";
            case TUESDAY -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY -> "목요일";
            case FRIDAY -> "금요일";
            case SATURDAY -> "토요일";
            case SUNDAY -> "일요일";
        };
    }

    private Map<String, Map<String, StatisticsDto>> convertHourlyStatsToMap(List<HourStatDto> stats) {
        Map<String, Map<String, StatisticsDto>> result = new LinkedHashMap<>();

        for (HourStatDto stat : stats) {
            String timePeriod = stat.getTimePeriod().getDisplayName();

            result.computeIfAbsent(timePeriod, k -> new LinkedHashMap<>())
                .put(stat.getActionType(),
                    new StatisticsDto(stat.getTotalActionCount(), stat.getUniqueUserCount()));
        }

        return result;
    }


    private Map<String, Map<String, StatisticsDto>> calculateHourlyAverages(
        Map<String, Map<String, StatisticsDto>> totals,
        PeriodInfoDto sixMonths
    ) {
        Map<String, Map<String, StatisticsDto>> averages = new LinkedHashMap<>();
        // 6개월 동안의 각 시간대 수 => 일수와 동일
        long dayCountInSixMonths = sixMonths.getStartDate().until(sixMonths.getEndDate(), java.time.temporal.ChronoUnit.DAYS) + 1;

        for (String timePeriod : totals.keySet()) {
            Map<String, StatisticsDto> hourlyStats = totals.get(timePeriod);
            Map<String, StatisticsDto> averageStats = new LinkedHashMap<>();

            for (String actionType : hourlyStats.keySet()) {
                StatisticsDto totalStats = hourlyStats.get(actionType);
                double averageActions = totalStats.getActionCount() / dayCountInSixMonths;
                double averageUsers = totalStats.getUniqueUserCount() / dayCountInSixMonths;

                averageStats.put(actionType, new StatisticsDto(averageActions, averageUsers));
            }

            averages.put(timePeriod, averageStats);
        }

        return averages;
    }


}

