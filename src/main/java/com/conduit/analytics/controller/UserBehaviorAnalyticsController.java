package com.conduit.analytics.controller;

import com.conduit.analytics.dto.response.FilteredUserCountResponseDto;
import com.conduit.analytics.dto.response.HourlyStatsResponseDto;
import com.conduit.analytics.dto.response.WeeklyStatsResponseDto;
import com.conduit.analytics.service.UserBehaviorAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class UserBehaviorAnalyticsController {

    private final UserBehaviorAnalyticsService analyticsService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics API is ok!");
    }

    @GetMapping("/statistics/weekly")
    public ResponseEntity<WeeklyStatsResponseDto> getWeeklyStats() {
        log.info("요일별 사용자 행동 통계 API 호출");

        try {
            WeeklyStatsResponseDto response = analyticsService.getWeeklyStats();
            log.info("요일별 통계 조회 완료");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("요일별 통계 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/statistics/hourly")
    public ResponseEntity<HourlyStatsResponseDto> getHourlyStats() {
        log.info("시간대별 사용자 행동 통계 API 호출");

        try {
            HourlyStatsResponseDto response = analyticsService.getHourlyStats();
            log.info("시간대별 통계 조회 완료");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("시간대별 통계 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("users/vote-active/count")
    public ResponseEntity<FilteredUserCountResponseDto> getFilteredUserCount() {
        log.info("특정 타겟 액션 사용자 필터링 API 호출");

        try {
            FilteredUserCountResponseDto response = analyticsService.getVoteUsersWithRecentActivity();
            log.info("필터링된 사용자 수 조회 완료: {} 명", response.getCount());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("필터링된 사용자 수 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
