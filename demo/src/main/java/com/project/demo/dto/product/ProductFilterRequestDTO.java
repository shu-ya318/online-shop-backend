package com.project.demo.dto.product;

import com.project.demo.enumeration.Category;

public record ProductFilterRequestDTO(
	String keyword,
    Category category) {
}
