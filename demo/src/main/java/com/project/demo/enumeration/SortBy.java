package com.project.demo.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortBy {
    TOTAL_SOLD("totalSold"),
    UPDATED_AT("updatedAt");

    private final String field;
}
