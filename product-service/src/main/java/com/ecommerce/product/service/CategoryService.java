package com.ecommerce.product.service;

import com.ecommerce.product.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
		CategoryDto createCategory(CategoryDto dto);
		CategoryDto updateCategory(Long id, CategoryDto dto);
		void deleteCategory(Long id);
		CategoryDto getCategory(Long id);
		List<CategoryDto> getAllCategories();
}
