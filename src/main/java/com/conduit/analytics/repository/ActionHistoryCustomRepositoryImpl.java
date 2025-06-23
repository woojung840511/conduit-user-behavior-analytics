package com.conduit.analytics.repository;


import com.conduit.analytics.dto.repository.HourStatDto;
import com.conduit.analytics.dto.repository.WeeklyStatDto;
import com.conduit.analytics.entity.QActionHistory;
import com.conduit.analytics.entity.QUserLastAction;
import com.conduit.analytics.enums.TimePeriod;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ActionHistoryCustomRepositoryImpl implements ActionHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WeeklyStatDto> getWeeklyActionStats(LocalDateTime from, LocalDateTime to) {
        QActionHistory ah = QActionHistory.actionHistory;

        return queryFactory
            .select(
                Projections.constructor(WeeklyStatDto.class,
                    ah.actionTime.dayOfWeek(),
                    ah.actionType,
                    ah.actionCount.sum(),
                    ah.userId.countDistinct()
                ))
            .from(ah)
            .where(
                ah.actionTime.between(from, to))
            .groupBy(ah.actionTime.dayOfWeek(), ah.actionType)
            .orderBy(ah.actionTime.dayOfWeek().asc(), ah.actionType.asc())
            .fetch();
    }

    @Override
    public List<HourStatDto> getHourStats(LocalDateTime from, LocalDateTime to) {
        QActionHistory ah = QActionHistory.actionHistory;

        NumberExpression<Integer> hour = ah.actionTime.hour();

        StringExpression timePeriodExpression = new CaseBuilder()
            .when(hour.between(TimePeriod.DAWN.getStartHour(), TimePeriod.DAWN.getEndHour()))
            .then(TimePeriod.DAWN.getDisplayName())
            .when(hour.between(TimePeriod.MORNING.getStartHour(), TimePeriod.MORNING.getEndHour()))
            .then(TimePeriod.MORNING.getDisplayName())
            .when(hour.between(TimePeriod.AFTERNOON.getStartHour(), TimePeriod.AFTERNOON.getEndHour()))
            .then(TimePeriod.AFTERNOON.getDisplayName())
            .otherwise(TimePeriod.NIGHT.getDisplayName());

        return queryFactory
            .select(
                Projections.constructor(HourStatDto.class,
                    timePeriodExpression,
                    ah.actionType,
                    ah.actionCount.sum(),
                    ah.userId.countDistinct()
                ))
            .from(ah)
            .where(ah.actionTime.between(from, to))
            .groupBy(timePeriodExpression, ah.actionType)
//            .orderBy(timePeriodExpression.asc(), ah.actionType.asc())
            .fetch();
    }

    @Override
    public long countVoteUsersWithRecentActivity() {
        QActionHistory ah = QActionHistory.actionHistory;
        QUserLastAction la = QUserLastAction.userLastAction;

        Long vote = queryFactory
            .select(ah.userId.countDistinct())
            .from(ah)
            .join(la).on(ah.userId.eq(la.userId))
            .where(
                ah.actionType.eq("VOTE"), // 투표한 사용자
                ah.actionTime.goe(LocalDateTime.now().minusMonths(6)),   // 최근 6개월 이내 투표한 사용자
                la.lastActionTime.goe(LocalDateTime.now().minusMonths(3)) // 최근 3개월 이내 활동한 사용자
            )
            .fetchOne();

        return vote == null ? 0 : vote;
    }
}
