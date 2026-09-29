package com.ecommerce.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product.dto.CategoryDto;
import com.ecommerce.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
		private final CategoryService categoryService;
		
		@PostMapping
		public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto){
			return ResponseEntity.ok(categoryService.createCategory(dto));
		}
		
		@GetMapping
		public ResponseEntity<List<CategoryDto>> getAllCategories(){
			return ResponseEntity.ok(categoryService.getAllCategories());
		}
		@GetMapping("/{id}")
		public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id){
			return ResponseEntity.ok(categoryService.getCategory(id));
		}
		@PutMapping("/{id}")
		public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto dto){
			return ResponseEntity.ok(categoryService.updateCategory(id, dto));
		}
		@DeleteMapping("/{id}")
		public ResponseEntity<String> deleteCategory(@PathVariable Long id){
			categoryService.deleteCategory(id);
			return ResponseEntity.ok("Category deleted successfully");
		}
		
		
		
		
		
		
		
		
}
