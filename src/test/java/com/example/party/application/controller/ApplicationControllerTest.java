package com.example.party.application.controller;

import static java.lang.String.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.ResultActions;

import com.example.party.TestHelper;
import com.example.party.application.service.ApplicationService;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.user.entity.User;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ApplicationController.class)
class ApplicationControllerTest {
	@MockBean
	private ApplicationService applicationService;
	@Autowired
	private MockMvc mockMvc;

	@LocalServerPort
	private String port;

	private URI uri(String path) throws URISyntaxException {
		return new URI(format("http://13.124.4.58:%s/api/applications%s", port, path));
	}

	@Nested
	@DisplayName("성공 테스트 입니다.")
	class Success {
		@BeforeEach
		public void init() {
			TestHelper.withoutSecurity();
		}

		@Test
		void createApplication() throws Exception {
			//  given

			//  when
			when(applicationService.createApplication(anyLong(), any(User.class)))
				.thenReturn(ApiResponse.ok("참가 신청 완료"));

			mockMvc.perform(post(uri("/join/123")))
				.andDo(print())
				.andExpect(status().isOk());
			//  then
			verify(applicationService).createApplication(anyLong(), any(User.class));
		}

		@DisplayName("나의 모임 참가 신청 취소하기")
		@Test
		void cancelApplication() throws Exception {
			//  given

			//  when
			when(applicationService.cancelApplication(anyLong(), any(User.class)))
				.thenReturn(ApiResponse.ok("참가 신청 취소 완료"));

			mockMvc.perform(post(uri("/cancel/999")))
				.andDo(print())
				.andExpect(status().isOk());
			//  then
			verify(applicationService).cancelApplication(anyLong(), any(User.class));
		}

		@DisplayName("내가 주최한 모임의 지원한 사람의 목록을 가져온다.")
		@Test
		void getApplications() throws Exception {
			//  given

			//  when
			mockMvc.perform(get("/api/applications/775"))
				.andExpect(status().isOk());

			when(applicationService.getApplications(anyLong(), any(User.class)))
				.thenReturn(DataApiResponse.ok("참가신청자 목록 조회 완료", Collections.emptyList()));
			//  then
			verify(applicationService).getApplications(anyLong(), any(User.class));
		}

		@DisplayName("내가 주최한 모임의 지원자를 받아들인다.")
		@Test
		void acceptApplication() throws Exception {
			//  given
			given(applicationService.acceptApplication(anyLong(), any(User.class)))
				.willReturn(ApiResponse.ok("테스트 성공"));

			//  when
			ResultActions actions = mockMvc.perform(post(uri("/accept/999"))
				.contentType(MediaType.APPLICATION_JSON));

			//  then
			actions.andDo(print())
				.andExpect(status().isOk());

			verify(applicationService).acceptApplication(anyLong(), any(User.class));
		}

		@DisplayName("내가 주최한 모임의 지원자를 거절한다.")
		@Test
		void rejectApplication() throws Exception {
			//  given
			given(applicationService.rejectApplication(anyLong(), any(User.class)))
				.willReturn(ApiResponse.ok("테스트 성공"));

			//  when
			ResultActions actions = mockMvc.perform(post(uri("/reject/999"))
				.contentType(MediaType.APPLICATION_JSON));

			//  then
			actions.andDo(print())
				.andExpect(status().isOk());

			verify(applicationService).rejectApplication(anyLong(), any(User.class));
		}
	}

	@Nested
	class Fail {
		@Test
		void createApplication_strangePathValue() throws Exception {
			//  given

			//  when
			ResultActions actions = mockMvc.perform(post(uri("/join"))
				.contentType(MediaType.APPLICATION_JSON));
			//  then
			actions.andDo(print())
				.andExpect(status().is4xxClientError());
		}

		@Test
		void cancelApplication_strangePathValue() throws Exception {
			//  given
			//  when
			ResultActions actions = mockMvc.perform(post(uri("/cancel"))
				.contentType(MediaType.APPLICATION_JSON));
			//  then
			actions.andDo(print())
				.andExpect(status().is4xxClientError());
		}

		@Test
		void getApplications_strangePathValue() throws Exception {
			//  given
			//  when
			ResultActions actions = mockMvc.perform(get(uri(""))
				.contentType(MediaType.APPLICATION_JSON));
			//  then
			actions.andDo(print())
				.andExpect(status().is4xxClientError());
		}

		@Test
		void acceptApplication_strangePathValue() throws Exception {
			//  given
			//  when
			ResultActions actions = mockMvc.perform(post(uri("/accept"))
				.contentType(MediaType.APPLICATION_JSON));
			//  then
			actions.andDo(print())
				.andExpect(status().is4xxClientError());
		}

		@Test
		void rejectApplication_strangePathValue() throws Exception {
			//  given
			//  when
			ResultActions actions = mockMvc.perform(post(uri("/accept"))
				.contentType(MediaType.APPLICATION_JSON));
			//  then
			actions.andDo(print())
				.andExpect(status().is4xxClientError());
		}
	}
}