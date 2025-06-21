package com.conduit.analytics.repository;

import com.conduit.analytics.dto.repository.HourStatDto;
import com.conduit.analytics.dto.repository.WeeklyStatDto;
import java.time.LocalDateTime;
import java.util.List;

public interface ActionHistoryCustomRepository {

    // 요일별 action_type별 통계 조회
    List<WeeklyStatDto> getWeeklyActionStats(LocalDateTime from, LocalDateTime to);

    // 시간대별 action_type별 통계 조회
    List<HourStatDto> getHourStats(LocalDateTime from, LocalDateTime to);

    // 최근 6개월간 vote한 사용자 중 최근 3개월 내 활동한 사용자 수
    long countVoteUsersWithRecentActivity();
}
