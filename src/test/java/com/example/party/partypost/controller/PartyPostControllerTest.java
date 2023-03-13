package com.example.party.partypost.controller;

import com.example.party.TestHelper;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.common.ItemApiResponse;
import com.example.party.partypost.PartyPostTestHelper;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.PartyPostResponse;
import com.example.party.partypost.dto.UpdatePartyPostRequest;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.service.PartyPostService;
import com.example.party.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static java.lang.String.format;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PartyPostController.class)
class PartyPostControllerTest {

	@MockBean
	private PartyPostService partyPostService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@LocalServerPort
	private String port;

	private URI uri(String path) throws URISyntaxException {
		return new URI(format("http://13.124.4.58:%s/api/party-posts%s", port, path));
	}

	@Nested
	class Success {
		@BeforeEach
		public void init() {
			TestHelper.withoutSecurity();
		}

		@Test
		void createPartyPost() throws Exception {
			//given

			// when
			when(partyPostService.createPartyPost(any(User.class), any(PartyPostRequest.class)))
				.thenReturn(ApiResponse.ok("모집글 작성 완료"));

			mockMvc.perform(post(uri(""))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(
						new PartyPostRequest(
							"title",
							"content",
							1L,
							(byte)3,
							"2023-12-14 16:00",
							"서울특별시 중랑구 망우동",
							"조앤조")))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
			// then
			verify(partyPostService).createPartyPost(any(User.class), any(PartyPostRequest.class));
		}

		@Test
		void updatePartyPost() throws Exception {
		//  given

		//  when
			when(partyPostService.updatePartyPost(anyLong(), any(UpdatePartyPostRequest.class), any(User.class)))
				.thenReturn(ApiResponse.ok("모집글 수정 완료"));

			mockMvc.perform(patch(uri("/999"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new UpdatePartyPostRequest(
					"", "", 1L, "", "")))
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
		//  then
			verify(partyPostService).updatePartyPost(anyLong(), any(UpdatePartyPostRequest.class), any(User.class));
		}

		@Test
		void findMyCreatedPartyList() throws Exception{
		//  given

		//  when
			when(partyPostService.findMyCreatedPartyList(any(User.class), anyInt()))
				.thenReturn(DataApiResponse.ok("내가 작성한 모집글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/mylist"))
					.param("page", "999")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
		//  then
			verify(partyPostService).findMyCreatedPartyList(any(User.class), anyInt());
		}
		@Test
		void findMyJoinedPartyList() throws Exception{
		//  given

		//  when
			when(partyPostService.findMyJoinedPartyList(any(User.class), anyInt()))
				.thenReturn(DataApiResponse.ok("내가 참가한 모집글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/my-join-list"))
				.param("page", "999")
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
		//  then
			verify(partyPostService).findMyJoinedPartyList(any(User.class), anyInt());
		}

		@Test
		void toggleLikePartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.toggleLikePartyPost(anyLong(), any(User.class)))
				.thenReturn(ApiResponse.ok("모집글 좋아요 완료"));

			mockMvc.perform(post(uri("/995/likes")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
		//  then
			verify(partyPostService).toggleLikePartyPost(anyLong(), any(User.class));
		}

		@Test
		void getLikePartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.getLikePartyPost(any(User.class)))
				.thenReturn(DataApiResponse.ok("좋아요 게시글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/likes")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
		//  then
			verify(partyPostService).getLikePartyPost(any(User.class));
		}

		@Test
		void findPartyList() throws Exception{
		//  given

		//  when
			when(partyPostService.findPartyList(any(User.class), anyInt()))
				.thenReturn(DataApiResponse.ok("모집글 전체 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri(""))
				.param("page", "999")
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
		//  then
			verify(partyPostService).findPartyList(any(User.class), anyInt());
		}

		@Test
		void getPartyPost() throws Exception{
		//  given
			PartyPost partyPost = PartyPostTestHelper.partyPost1();
			PartyPostResponse partyPostResponse = new PartyPostResponse(partyPost);
			//  when
			when(partyPostService.getPartyPost(anyLong(), any(User.class)))
				.thenReturn(ItemApiResponse.ok("모집글 상세 조회 완료", partyPostResponse));

			mockMvc.perform(get(uri("/995")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isNotEmpty());
		//  then
			verify(partyPostService).getPartyPost(anyLong(), any(User.class));
		}

		@Test
		void deletePartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.deletePartyPost(anyLong(), any(User.class)))
				.thenReturn(ApiResponse.ok("모집글 삭제 완료"));

			mockMvc.perform(delete(uri("/995")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
		//  then
			verify(partyPostService).deletePartyPost(anyLong(), any(User.class));
		}

		@Test
		void searchPartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.searchPartyPost(any(User.class), anyString(), anyInt()))
				.thenReturn(DataApiResponse.ok("모집글 검색 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/search"))
				.param("searchText", "searchText")
				.param("page", "999")
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
		//  then
			verify(partyPostService).searchPartyPost(any(User.class), anyString(), anyInt());
		}

		@Test
		void findHotPartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.findHotPartyPost(any(User.class)))
				.thenReturn(DataApiResponse.ok("핫한 모집글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/hot")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString());
		//  then
			verify(partyPostService).findHotPartyPost(any(User.class));
		}

		@Test
		void searchPartyPostByCategory() throws Exception{
			//  given

			//  when
			when(partyPostService.searchPartyPostByCategory(any(User.class), anyLong(), anyInt()))
				.thenReturn(DataApiResponse.ok("카테고리별 모집글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/categories/995"))
					.param("page", "999")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
			//  then
			verify(partyPostService).searchPartyPostByCategory(any(User.class), anyLong(), anyInt());
		}

		@Test
		void findNearPartyPost() throws Exception{
		//  given

		//  when
			when(partyPostService.findNearPartyPost(any(User.class), anyString()))
				.thenReturn(DataApiResponse.ok("주변 모집글 조회 완료", Collections.emptyList()));

			mockMvc.perform(get(uri("/near/서울시중랑구망우동")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.msg").isString())
				.andExpect(jsonPath("$.data").isArray());
		//  then
			verify(partyPostService).findNearPartyPost(any(User.class), anyString());
		}

	}
}