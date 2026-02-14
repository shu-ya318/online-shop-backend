package com.project.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.project.demo.converter.StringToCategoryConverter;
import com.project.demo.converter.StringToSortDirectionConverter;
import com.project.demo.converter.StringToSortByConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new StringToCategoryConverter());
        registry.addConverter(new StringToSortDirectionConverter());
        registry.addConverter(new StringToSortByConverter());
    }
}
