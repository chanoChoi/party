package com.example.party.category.controller;

import static java.lang.String.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.category.service.CategoryService;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
	@MockBean
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private String port;

	private URI uri(String path) throws URISyntaxException {
		return new URI(format("http://13.124.4.58:%s/api/categories%s", port, path));
	}

	@DisplayName("SUCCESS")
	@Nested
	public class SUCCESS {
		@Test
		void createCategory() throws Exception {
			//  given
			//  when
			when(categoryService.createCategory(any(CategoryRequest.class)))
				.thenReturn(ApiResponse.ok("카테고리 생성 완료"));

			mockMvc.perform(post(uri(""))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(new CategoryRequest("test")))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").value("카테고리 생성 완료"));
			//  then
			verify(categoryService).createCategory(any(CategoryRequest.class));
		}

		@Test
		void getCategory() throws Exception {
			//  given
			//  when
			when(categoryService.getCategory())
				.thenReturn(DataApiResponse.ok("카테고리 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").value("카테고리 조회 완료"))
				.andExpect(jsonPath("$.data").isArray());

			//  then
			verify(categoryService).getCategory();
		}

		@Test
		void updateCategory() throws Exception {
			//  given

			//  when
			when(categoryService.updateCategory(anyLong(), any(CategoryRequest.class)))
				.thenReturn(ApiResponse.ok("카테고리 수정 완료"));

			mockMvc.perform(patch(uri("/999"))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(new CategoryRequest("test")))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").value("카테고리 수정 완료"));
			//  then
			verify(categoryService).updateCategory(anyLong(), any(CategoryRequest.class));
		}

		@Test
		void deleteCategory() throws Exception {
			//  given

			//  when
			when(categoryService.deleteCategory(anyLong()))
				.thenReturn(ApiResponse.ok("카테고리 삭제 완료"));

			mockMvc.perform(delete(uri("/999")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").value("카테고리 삭제 완료"));
			//  then
			verify(categoryService).deleteCategory(anyLong());
		}
	}

}
