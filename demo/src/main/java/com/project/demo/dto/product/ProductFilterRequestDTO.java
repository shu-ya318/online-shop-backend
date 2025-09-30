package com.project.demo.dto.product;

import com.project.demo.enumeration.Category;
import jakarta.validation.constraints.Pattern;

public record ProductFilterRequestDTO(
	@Pattern(regexp = "^(?!\\s*$).+", message = "Keyword cannot be empty or blank!")
	String keyword,

	Category category) {
}
