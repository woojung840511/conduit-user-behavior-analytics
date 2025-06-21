package com.conduit.analytics.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimePeriod {

    DAWN("새벽", 0, 5, 1),
    MORNING("오전", 6, 11, 2),
    AFTERNOON("오후", 12, 17, 3),
    NIGHT("야간", 18, 23, 4);

    private final String displayName;
    private final int startHour;
    private final int endHour;
    private final int sortOrder;

    public static TimePeriod fromName(String displayName) {
        return Arrays.stream(values())
            .filter(period -> period.getDisplayName().equals(displayName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("알 수 없는 시간대: " + displayName));
    }

}
