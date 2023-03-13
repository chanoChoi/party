package com.example.party.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.category.dto.CategoryResponse;
import com.example.party.category.service.CategoryService;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	//카테고리 작성
	@PostMapping
	public ResponseEntity<ApiResponse> createCategory(
		@RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.createCategory(request));
	}

	//카테고리 조회
	@GetMapping("")
	public ResponseEntity<DataApiResponse<CategoryResponse>> getCategory() {
		return ResponseEntity.ok(categoryService.getCategory());
	}

	//카테고리 수정
	@PatchMapping("/{categoryId}")
	public ResponseEntity<ApiResponse> updateCategory(
		@PathVariable(name = "categoryId") Long categoryId, @RequestBody CategoryRequest request) {
		return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
	}

	//카테고리 삭제
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse> deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
		return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
	}
}
