package com.project.demo.dto.project;

import com.project.demo.enumeration.Category;

public record ProductFilterRequestDTO(
	String keyword,
    Category category) {
}
