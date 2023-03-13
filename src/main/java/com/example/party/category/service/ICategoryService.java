package com.example.party.category.service;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.category.dto.CategoryResponse;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;

public interface ICategoryService {

	//카테고리 생성
	ApiResponse createCategory(CategoryRequest request);

	//카테고리 조회
	DataApiResponse<CategoryResponse> getCategory();

	//카테고리 수정
	ApiResponse updateCategory(Long categoryId, CategoryRequest request);

	//카테고리 삭제
	ApiResponse deleteCategory(Long CategoryId);
}
