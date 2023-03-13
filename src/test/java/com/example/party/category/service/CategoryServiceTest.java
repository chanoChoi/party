package com.example.party.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.category.dto.CategoryResponse;
import com.example.party.category.entity.Category;
import com.example.party.category.exception.CategoryNotFoundException;
import com.example.party.category.exception.DuplicateNameNotAllowException;
import com.example.party.category.repository.CategoryRepository;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService categoryService;

	private Category category;
	private CategoryRequest categoryRequest;

	@BeforeEach
	public void setUp() {
		this.category = mock(Category.class);
		this.categoryRequest = mock(CategoryRequest.class);
	}

	@Test
	void createCategory() {
		//  given
		//  when
		when(categoryRequest.getName()).thenReturn("TEST");
		when(categoryRepository.existsCategoryByName(anyString())).thenReturn(false);

		ApiResponse result = categoryService.createCategory(categoryRequest);
		//  then
		verify(categoryRepository).existsCategoryByName(anyString());
		verify(categoryRepository).save(any(Category.class));
		assertThat(result.getCode()).isEqualTo(201);
		assertThat(result.getMsg()).isEqualTo("카테고리 생성 완료");
	}

	@Test
	void getCategory() {
		//  given
		//  when
		when(categoryRepository.findAllByActiveIsTrue()).thenReturn(Collections.emptyList());

		DataApiResponse<CategoryResponse> result = categoryService.getCategory();
		//  then
		verify(categoryRepository).findAllByActiveIsTrue();
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("카테고리 조회 완료");
		assertThat(result.getData()).isEmpty();
	}

	@Test
	void updateCategory() {
		//  given
		//  when
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(categoryRequest.getName()).thenReturn("TEST");
		when(categoryRepository.existsCategoryByName(anyString())).thenReturn(false);

		ApiResponse result = categoryService.updateCategory(category.getId(), categoryRequest);
		//  then
		verify(categoryRepository).findById(anyLong());
		verify(categoryRepository).existsCategoryByName(anyString());
		verify(category).update(categoryRequest);
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("카테고리 수정 완료");
	}

	@Test
	void deleteCategory() {
		//  given
		//  when
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

		ApiResponse result = categoryService.deleteCategory(category.getId());
		//  then
		verify(categoryRepository).findById(anyLong());
		verify(category).deleteCategory();
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("카테고리 삭제 완료");
	}

	// Throw Exception

	@Test
	void createCategory_DuplicateNameNotAllowException() {
		//  given
		//  when
		when(categoryRequest.getName()).thenReturn("TEST");
		when(categoryRepository.existsCategoryByName(anyString())).thenReturn(true);
		var thrown = assertThatThrownBy(
			() -> categoryService.createCategory(categoryRequest));
		//  then
		verify(categoryRepository).existsCategoryByName(anyString());
		thrown.isInstanceOf(DuplicateNameNotAllowException.class)
			.hasMessageContaining(DuplicateNameNotAllowException.MSG);
	}

	@Test
	void updateCategory_DuplicateNameNotAllowException() {
		//  given

		//  when
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(categoryRequest.getName()).thenReturn("TEST");
		when(categoryRepository.existsCategoryByName(anyString())).thenReturn(true);
		var thrown = assertThatThrownBy(
			() -> categoryService.updateCategory(category.getId(), categoryRequest));
		//  then
		verify(categoryRepository).findById(anyLong());
		verify(categoryRepository).existsCategoryByName(anyString());
		thrown.isInstanceOf(DuplicateNameNotAllowException.class)
			.hasMessageContaining(DuplicateNameNotAllowException.MSG);
	}

	@Test
	void getCategory_CategoryNotFoundException() {
		//  given

		//  when
		var  thrown = assertThatThrownBy(
			() -> categoryService.getCategory(category.getId()));
		//  then
		thrown.isInstanceOf(CategoryNotFoundException.class)
			.hasMessage(CategoryNotFoundException.MSG);
	}
}