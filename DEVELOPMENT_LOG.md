# 컨두잇 과제 개발 로그

## 📋 프로젝트 개요
- **과제명**: 시간 기반 사용자 행동 통계 및 필터링 시스템 구현
- **기술스택**: Spring Boot 3.5.0, QueryDSL, JPA, H2
- **개발기간**: 2025.06.16 ~

---

## 🤔 주요 기술적 결정사항

### 2025.06.17

#### Entity 설계 - ActionHistory.actionCount
**고민**: 로그 데이터인데 왜 count 필드가 있을까? (대부분 1이 될 것으로 예상)
**결정**: 그대로 유지
**근거**:
- 집계된 데이터 저장 가능성
- 과제 스키마 준수

#### 통계 데이터 해석 (미결정)
**고민**: "요일별 고유 사용자 수"의 의미
- A안: action_type별 고유 사용자 수
- B안: 요일별 전체 고유 사용자 수 (action_type 통합)

**현재 상태**: A안으로 구현 진행 중
**추후 검토 필요**: 실제 비즈니스 요구사항 확인

#### "최근 일주일간" 기간 해석
**고민**: 이번주 vs 지난주 vs 조회시점 기준 7일
**결정**: 지난주 일요일~토요일 (완료된 주간)
**근거**:
- 비교 가능한 완전한 데이터
- 일반적인 비즈니스 관례
- 분석의 일관성

#### 통계 쿼리 설계
- 요일별 action_type별 통계 쿼리
  - 우선 DB에서는 기간별로 항목의 총합을 가져오고, 애플리케이션에서 기간에 해당하는 요일수로 나누기
    ```sql
    # 조회 기간 : 최근 1주일간 (완료된 지난주, 즉 최근 일요일부터 토요일까지) 혹은 6개월
    # 우선 요일별로 (action_type 별 action_count 총합과 고유 사용자수 가져오기)
    select 
        day_of_week,
        action_type,
        sum(action_count) as total_action_count,
        count(distinct user_id) as unique_users
    from (
        select 
            EXTRACT(DOW from action_time) as day_of_week,
            action_type,
            action_count,
            user_id
        from action_history
        where action_time between ? and ? # 조회 기간
    ) data_in_period
    group by day_of_week, action_type
    order by day_of_week, action_type;
    ```
- 시간대별 action_type별 통계 쿼리
  - 우선 DB에서는 기간별로 항목의 총합을 가져오고, 애플리케이션에서 기간에 해당하는 시간대별 수로 나누기
  - 시간대는 새벽(0-5), 오전(6-11), 오후(12-17), 야간(18-23)으로 구분
    ```sql
    # 조회 기간 : 어제 하루 or 최근 6개월간
    # 시간대별로 (action_type 별 action_count 총합과 고유 사용자수 가져오기)
    select 
        time_period,
        action_type,
        sum(action_count) as total_action_count,
        count(distinct user_id) as unique_users
    from (
        select 
            case 
                when EXTRACT(HOUR from action_time) between 0 and 5 then '새벽'
                when EXTRACT(HOUR from action_time) between 6 and 11 then '오전'
                when EXTRACT(HOUR from action_time) between 12 and 17 then '오후'
                else '야간'
              end as time_period,
            action_type,
            action_count,
            user_id
        from action_history
        where action_time between ? and ?
    ) data_in_period
    group by time_period, action_type
    order by 
        case time_period 
            when '새벽' then 1
            when '오전' then 2  
            when '오후' then 3
            when '야간' then 4
        end,
        action_type;
    ```

- 특정 타겟 액션 사용자 필터링
  ```sql
  # 방법 1
  select count(distinct ah.user_id) as filtered_user_count
  from action_history as ah
  inner join user_last_action as ula on ah.user_id = ula.user_id
  where ah.action_target = 'vote' 
    and ah.action_time >= now() - interval '6 months'
    and ula.last_action_time >= now() - interval '3 months';
  
  # 방법 2
  select count(*) as filtered_user_count
  from (
      -- 6개월간 vote한 사용자들
      select distinct user_id 
      from action_history 
      where action_target = 'vote' 
        and action_time >= now() - interval '6 months'
  ) vote_users
  inner join user_last_action ula on vote_users.user_id = ula.user_id
  where ula.last_action_time >= now() - interval '3 months';
  ```
---

## 🏗️ 아키텍처 결정사항

### 응답 JSON 구조
```json
{
  "weekly_stats": {
    "월요일": {
      "VIEW": { "action_count": 1500, "unique_users": 350 }
    }
  }
}
```


---

## 🚀 다음 단계
- [x] Entity 설계 및 JPA 매핑
- [x] SQL 쿼리 설계
- [ ] Repository 계층 구현
- [ ] QueryDSL 복잡 쿼리 작성
- [ ] Service 비즈니스 로직 구현
- [ ] Controller API 엔드포인트 구현
- [ ] 테스트 데이터 및 단위 테스트 작성