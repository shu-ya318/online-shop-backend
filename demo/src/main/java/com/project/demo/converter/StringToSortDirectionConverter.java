package com.project.demo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class StringToSortDirectionConverter implements Converter<String, Sort.Direction> {

    @Override
    public Sort.Direction convert(@NonNull String source) {
        return Sort.Direction.valueOf(source.trim().toUpperCase());
    }
}
