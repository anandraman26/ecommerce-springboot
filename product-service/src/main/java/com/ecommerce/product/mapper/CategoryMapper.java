package com.ecommerce.product.mapper;

import org.springframework.stereotype.Component;

import com.ecommerce.product.dto.CategoryDto;
import com.ecommerce.product.entity.Category;

@Component
public class CategoryMapper {
		public CategoryDto toDto(Category category) {
			return CategoryDto.builder()
					.id(category.getId())
					.name(category.getName())
					.description(category.getDescription())
					.parentId(category.getParentId())
					.build();
		}
		public Category toEntity(CategoryDto dto) {
			return Category.builder()
					.name(dto.getName())
					.description(dto.getDescription())
					.parentId(dto.getParentId())
					.build();
		}
}
