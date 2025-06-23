# 대용량 데이터 성능 최적화 전략

## 1. 데이터베이스 레벨 최적화

### 1.1 인덱스 최적화
```java
@Entity
@Table(name = "action_history", indexes = {
    @Index(name = "idx_action_time", columnList = "action_time"),
    @Index(name = "idx_actiontype_time_userid", columnList = "action_type, action_time, user_id")
})
public class ActionHistory { /* 기존 코드 */ }

@Entity  
@Table(name = "user_last_action", indexes = {
    @Index(name = "idx_last_action_time", columnList = "last_action_time")
})
public class UserLastAction { /* 기존 코드 */ }
```
인덱스별 설계 근거
- idx_action_time (action_time)
  - 용도: 요일별/시간대별 통계 쿼리
  - 근거: WHERE절에 시간 범위 조건만 존재
- idx_actiontype_time_userid (action_type, action_time, user_id)
  - 용도: 투표 사용자 필터링 쿼리
  - 근거:
    - action_type='vote': 가장 선택적 (1순위)
    - action_time >= ?: 시간 범위 필터링 (2순위)
    - user_id: JOIN 최적화 + 커버링 인덱스 (3순위)
- idx_last_action_time (last_action_time)
  - 용도: 사용자 활동 시간 필터링
  - 근거: WHERE절에 활동 시간 조건만 존재 (user_id는 PK로 자동 인덱스)

### 1.2 파티셔닝
```sql
-- 날짜 기반 파티셔닝 (월별)
CREATE TABLE action_history (
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_target VARCHAR(100),
    action_count INT DEFAULT 1,
    action_value DOUBLE PRECISION,
    action_time TIMESTAMP NOT NULL
) PARTITION BY RANGE (action_time);

-- 월별 파티션 생성
CREATE TABLE action_history_202501 PARTITION OF action_history
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE action_history_202502 PARTITION OF action_history
    FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');
-- ... 계속
```

**장점**:
- 쿼리 시 필요한 파티션만 스캔
- 오래된 데이터 아카이빙 용이
- 병렬 처리 가능

### 1.3 샤딩 (Sharding)
```
User ID 기반 샤딩:
- Shard 1: user_id % 4 = 0
- Shard 2: user_id % 4 = 1  
- Shard 3: user_id % 4 = 2
- Shard 4: user_id % 4 = 3
```

---

## 2. 사전 집계

### 2.1 요일별 통계 사전 집계 테이블
```sql
CREATE TABLE daily_action_stats (
    stat_date DATE,
    day_of_week INTEGER,
    action_type VARCHAR(50),
    total_action_count BIGINT,
    unique_user_count BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (stat_date, action_type)
);
```

### 2.2 시간대별 통계 사전 집계 테이블
```sql
CREATE TABLE hourly_action_stats (
    stat_date DATE,
    time_period VARCHAR(10), -- '새벽', '오전', '오후', '야간'
    action_type VARCHAR(50),
    total_action_count BIGINT,
    unique_user_count BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (stat_date, time_period, action_type)
);
```

### 2.3 배치 집계 작업
```java
@Component
@Slf4j
public class StatisticsAggregationBatch {
    
    @Scheduled(cron = "0 5 * * * *") // 매일 새벽 5시
    public void aggregateDailyStats() {
        // 전날 데이터를 집계하여 사전 집계 테이블에 저장
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 1. 요일별 통계 집계
        aggregateWeeklyStats(yesterday);
        
        // 2. 시간대별 통계 집계  
        aggregateHourlyStats(yesterday);
        
        log.info("일별 통계 집계 완료: {}", yesterday);
    }
}
```

### 2.4 추후 사전집계를 활용해 쿼리 최적화
```sql
-- 기존 쿼리 (함수를 select 와 group by 에 중복 사용해서 비효율적)
SELECT 
    EXTRACT(DOW FROM action_time) as day_of_week,
    action_type,
    SUM(action_count) as total_count,
    COUNT(DISTINCT user_id) as unique_users
FROM action_history 
WHERE action_time BETWEEN ? AND ?
GROUP BY EXTRACT(DOW FROM action_time), action_type;

-- 최적화된 쿼리 (사전 집계 테이블 활용)
SELECT 
    day_of_week,
    action_type,
    SUM(total_action_count) as total_count,
    SUM(unique_user_count) as unique_users
FROM daily_action_stats
WHERE stat_date BETWEEN ? AND ?
GROUP BY day_of_week, action_type;
```
---

4억 건의 대용량 데이터를 효율적으로 처리하기 위해서는:
1. 인덱스 최적화
2. 사전 집계 테이블 구축
3. 파티셔닝 + 샤딩

단계적 접근을 통해 시스템의 안정성을 유지하면서 성능을 점진적으로 개선할 수 있습니다.