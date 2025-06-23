# 사용자 행동 통계 및 필터링 시스템

> 컨두잇 백엔드 개발자 과제 전형

## 📋 프로젝트 개요

대규모 사용자 로그(4억 건)를 분석하여 마케팅 전략 및 사용자 참여 지표를 제공하는 데이터 분석 시스템입니다.

- **일일 로그**: 약 100만 건
- **총 데이터**: 약 4억 건
- **목표**: 정확한 분석과 함께 성능과 확장성 고려

## 🛠 기술 스택

- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **QueryDSL 5.0.0**
- **H2 Database** (개발용)
- **Lombok**

## 🎯 주요 기능

### 1. 요일별 사용자 행동 통계
- 최근 1주일간 요일별 통계
- 최근 6개월간 요일별 평균
- action_type별 총 action_count 및 고유 사용자 수

### 2. 시간대별 사용자 행동 통계
- 어제 하루 시간대별 통계
- 최근 6개월간 시간대별 평균
- 시간대 구분: 새벽(00-05), 오전(06-11), 오후(12-17), 야간(18-23)

### 3. 특정 타겟 액션 사용자 필터링
- 최근 6개월간 투표한 사용자 중 최근 3개월 이내 활동한 사용자 수 조회

## 실행 방법

### H2 콘솔 접속
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (공백)

### API 테스트
```bash
# 헬스체크
GET http://localhost:8080/api/analytics/health

# 요일별 통계
GET http://localhost:8080/api/analytics/statistics/weekly

# 시간대별 통계  
GET http://localhost:8080/api/analytics/statistics/hourly

# 투표 사용자 필터링
GET http://localhost:8080/api/analytics/users/vote-active/count
```

## 📊 API 응답 예시

### 요일별 통계
```json
{
  "recent_week": {
    "period_info": {
      "period_type": "RECENT_WEEK",
      "description": "최근 일주일(최근 일요일부터 토요일까지)"
    },
    "results": {
      "월요일": {
        "VIEW": { "action_count": 1500, "unique_user_count": 350 }
      }
    }
  }
}
```

## ⚡ 성능 최적화

### 적용된 최적화
- **인덱스 최적화**: 쿼리 패턴 분석 기반 인덱스 설계
- **QueryDSL**: 복잡한 통계 쿼리 최적화
- **DTO 매핑**: 불필요한 데이터 전송 최소화

## 📚 문서

- [개발 로그](DEVELOPMENT_LOG.md) - 개발 과정과 기술적 결정사항
- [성능 최적화 전략](PERFORMANCE_OPTIMIZATION.md) - 대용량 데이터 처리 방안

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17+
- Gradle 8.14+

### 의존성 주요 버전
```gradle
- Spring Boot: 3.5.0
- QueryDSL: 5.0.0
- H2: runtime
- Lombok: compileOnly
```