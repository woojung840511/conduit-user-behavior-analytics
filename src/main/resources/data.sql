-- 최소한의 테스트 데이터 (H2 호환, 안전한 방식)

-- 1. 사용자 마지막 활동 시간 (10명만 - 확실히 작동하도록)
INSERT INTO user_last_action (user_id, last_action_time) VALUES
                                                             (1001, CURRENT_TIMESTAMP - INTERVAL '10' DAY),
                                                             (1002, CURRENT_TIMESTAMP - INTERVAL '20' DAY),
                                                             (1003, CURRENT_TIMESTAMP - INTERVAL '30' DAY),
                                                             (1004, CURRENT_TIMESTAMP - INTERVAL '5' DAY),
                                                             (1005, CURRENT_TIMESTAMP - INTERVAL '15' DAY),
                                                             (1006, CURRENT_TIMESTAMP - INTERVAL '25' DAY),
                                                             (1007, CURRENT_TIMESTAMP - INTERVAL '8' DAY),
                                                             (1008, CURRENT_TIMESTAMP - INTERVAL '12' DAY),
                                                             (1009, CURRENT_TIMESTAMP - INTERVAL '18' DAY),
                                                             (1010, CURRENT_TIMESTAMP - INTERVAL '22' DAY);

-- 2. 기본 행동 기록 (30건 - 간단하고 확실하게)
INSERT INTO action_history (user_id, action_type, action_target, action_count, action_value, action_time) VALUES
-- 최근 일주일 데이터 (요일별)
(1001, 'VIEW', 'product_123', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '1' DAY),  -- 월요일
(1002, 'CLICK', 'banner_01', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '2' DAY),  -- 화요일
(1003, 'VOTE', 'vote', 1, 4.5, CURRENT_TIMESTAMP - INTERVAL '3' DAY),        -- 수요일
(1004, 'VIEW', 'product_456', 2, NULL, CURRENT_TIMESTAMP - INTERVAL '4' DAY), -- 목요일
(1005, 'CLICK', 'menu_item', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '5' DAY),  -- 금요일
(1006, 'VOTE', 'vote', 1, 3.8, CURRENT_TIMESTAMP - INTERVAL '6' DAY),        -- 토요일
(1007, 'VIEW', 'product_789', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '7' DAY), -- 일요일

-- 어제 데이터 (시간대별)
(1001, 'VIEW', 'morning_content', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '16' HOUR), -- 새벽
(1002, 'CLICK', 'breakfast_ad', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '10' HOUR),   -- 오전
(1003, 'VOTE', 'vote', 1, 4.2, CURRENT_TIMESTAMP - INTERVAL '6' HOUR),            -- 오후
(1004, 'VIEW', 'evening_news', 1, NULL, CURRENT_TIMESTAMP - INTERVAL '2' HOUR),    -- 야간

-- 6개월간 VOTE 데이터
(1001, 'VOTE', 'vote', 1, 4.1, CURRENT_TIMESTAMP - INTERVAL '1' MONTH),
(1002, 'VOTE', 'vote', 1, 3.9, CURRENT_TIMESTAMP - INTERVAL '2' MONTH),
(1003, 'VOTE', 'vote', 1, 4.7, CURRENT_TIMESTAMP - INTERVAL '3' MONTH),
(1004, 'VOTE', 'vote', 1, 3.6, CURRENT_TIMESTAMP - INTERVAL '4' MONTH),
(1005, 'VOTE', 'vote', 1, 4.8, CURRENT_TIMESTAMP - INTERVAL '5' MONTH);

-- 3. 검증 쿼리
-- SELECT COUNT(*) FROM user_last_action;
-- SELECT COUNT(*) FROM action_history;
-- SELECT action_type, COUNT(*) FROM action_history GROUP BY action_type;