package com.project.demo.dto.common;

import java.util.List;

public record PaginatedResponse<T>(
		List<T> content, 
		int currentPage, 
		int size, 
		long totalElements, 
		int totalPages) {
}
