package com.conduit.analytics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilteredUserCountResponseDto {

    long count;         // 필터링된 사용자 수
    String description; // 필터링 기준

}
