package com.project.demo.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.project.demo.enumeration.Category;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {

    @Override
    public Category convert(@NonNull String source) {
        return Category.valueOf(source.trim().toUpperCase());
    }
}
