package com.project.demo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import com.project.demo.enumeration.SortBy;

public class StringToSortByConverter implements Converter<String, SortBy> {

    @Override
    public SortBy convert(@NonNull String source) {
        String trimmed = source.trim();
        for (SortBy sortBy : SortBy.values()) {
            if (sortBy.getField().equalsIgnoreCase(trimmed)) {
                return sortBy;
            }
        }
        // Fallback to strict uppercase matching if needed, or throw exception
        // But for our case, field matching (case insensitive) is safer for parameters
        try {
             return SortBy.valueOf(trimmed.toUpperCase());
        } catch(IllegalArgumentException e) {
             // If not found by field or name, return default or null or rethrow
             // Let's assume input matches one of the fields or names
             throw new IllegalArgumentException("Invalid sort by value: " + source);
        }
    }
}
