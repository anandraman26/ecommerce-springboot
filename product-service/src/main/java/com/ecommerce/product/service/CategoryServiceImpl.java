package com.ecommerce.product.service;

import com.ecommerce.product.dto.CategoryDto;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.mapper.CategoryMapper;
import com.ecommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
		private final CategoryRepository categoryRepo;
		private final CategoryMapper categoryMapper;
	@Override
	public CategoryDto createCategory(CategoryDto dto) {
		Category category  = categoryMapper.toEntity(dto);
		return categoryMapper.toDto(categoryRepo.save(category));
	}

	@Override
	public CategoryDto updateCategory(Long id, CategoryDto dto) {
		Category category = categoryRepo.findById(id).orElseThrow(()-> new RuntimeException("category not found"));
		category.setName(dto.getName());
		category.setDescription(dto.getDescription());
		category.setParentId(dto.getParentId());
		return categoryMapper.toDto(categoryRepo.save(category));
	}

	@Override
	public void deleteCategory(Long id) {
		categoryRepo.deleteById(id);
		
	}

	@Override
	public CategoryDto getCategory(Long id) {
		
		return categoryRepo.findById(id).map(categoryMapper::toDto).orElseThrow(()-> new RuntimeException("Category not found"));
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		// TODO Auto-generated method stub
		return categoryRepo.findAll()
				.stream().map(categoryMapper::toDto).toList();
	}

}
