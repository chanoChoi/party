package com.example.party.partypost.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.example.party.application.entity.Application;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.category.entity.Category;
import com.example.party.category.exception.CategoryNotActiveException;
import com.example.party.category.exception.CategoryNotFoundException;
import com.example.party.category.repository.CategoryRepository;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.common.ItemApiResponse;
import com.example.party.partypost.dto.MyPartyPostListResponse;
import com.example.party.partypost.dto.PartyPostListResponse;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.PartyPostResponse;
import com.example.party.partypost.dto.UpdatePartyPostRequest;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.IsNotWritterException;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.partypost.type.Status;
import com.example.party.user.entity.Profile;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PartyPostServiceTest {
	@Mock
	private PartyPostRepository partyPostRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ApplicationRepository applicationRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private PartyPostValidator partyPostValidator;
	@InjectMocks
	private PartyPostService partyPostService;

	private User user;
	private PartyPost partyPost;
	private PartyPostRequest partyPostRequest;
	private UpdatePartyPostRequest updatePartyPostRequest;
	private Category category;
	private PartyPostListResponse partyPostListResponse;

	@BeforeEach
	public void setup() {
		this.user = mock(User.class);
		this.partyPost = mock(PartyPost.class);
		this.partyPostRequest = mock(PartyPostRequest.class);
		this.updatePartyPostRequest = mock(UpdatePartyPostRequest.class);
		this.category = mock(Category.class);
		this.partyPostListResponse = mock(PartyPostListResponse.class);
	}

	@Test
	void createPartyPost() {
		//  given
		Category category = mock(Category.class);
		Application application = mock(Application.class);

		//  when
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
		when(partyPostRequest.getPartyDate()).thenReturn("2023-02-16 12:00"); // 오류 뜰까?
		when(partyPostRequest.getCategoryId()).thenReturn(999L);
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(category.isActive()).thenReturn(true);
		when(partyPostRequest.getPartyAddress()).thenReturn("서울 마포구 연남동 567-34");
		when(partyPostRequest.getPartyPlace()).thenReturn("파델라");
		when(partyPostRepository.save(any(PartyPost.class))).thenReturn(partyPost);

		ApiResponse result = partyPostService.createPartyPost(user, partyPostRequest);
		//  then
		verify(categoryRepository).findById(anyLong());
		verify(category).isActive();
		verify(partyPostRepository).save(any(PartyPost.class));
		assertThat(result.getCode()).isEqualTo(201);
		assertThat(result.getMsg()).isEqualTo("모집글 작성 완료");
	}

	@Test
	void findPartyList() {
		//  given

		//  when
		when(partyPostRepository.findAllByActiveIsTrue(any(Pageable.class))).thenReturn(Collections.emptyList());
		when(partyPostValidator.filteringPosts(any(User.class), anyList())).thenReturn(Collections.emptyList());

		DataApiResponse<PartyPostListResponse> result = partyPostService.findPartyList(user, 99);
		//  then
		verify(partyPostRepository).findAllByActiveIsTrue(any(Pageable.class));
		verify(partyPostValidator).filteringPosts(any(User.class), anyList());
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("모집글 전체 조회 완료");
		assertThat(result.getData()).isEmpty();
	}

	@Test
	void getPartyPost() {
		//  given
		Category category = mock(Category.class);
		Profile profile = mock(Profile.class);
		//  when
		when(partyPostRepository.findByIdAndActiveIsTrue(anyLong())).thenReturn(Optional.of(partyPost));
		when(partyPost.getUser()).thenReturn(user);
		when(user.getNickname()).thenReturn("nickname");
		when(partyPost.getCategory()).thenReturn(category);
		when(category.getId()).thenReturn(999L);
		LocalDateTime.parse("2023-02-16 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		when(user.getId()).thenReturn(1L);
		when(user.getProfile()).thenReturn(profile);
		when(profile.getProfileImg()).thenReturn("testImg");
		when(partyPost.getStatus()).thenReturn(Status.FINDING);
		when(partyPost.getPartyDate()).thenReturn(LocalDateTime.now());
		when(partyPost.getCloseDate()).thenReturn(LocalDateTime.now());

		ItemApiResponse<PartyPostResponse> result = partyPostService.getPartyPost(partyPost.getId(), user);
		//  then
		verify(partyPostRepository).findByIdAndActiveIsTrue(anyLong());
		verify(partyPost).increaseViewCnt(any(User.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("모집글 상세 조회 완료");
		assertThat(result.getData()).isNotNull();
	}

	@Test
	void searchPartyPost() {
		//  given

		//  when
		when(
			partyPostRepository.findByTitleContainingOrAddressContaining(anyString(), anyString(), any(Pageable.class)))
			.thenReturn(Collections.emptyList());
		when(partyPostValidator.filteringPosts(any(User.class), anyList())).thenReturn(Collections.emptyList());

		DataApiResponse<PartyPostListResponse> result = partyPostService.searchPartyPost(user, "searchText", 99);
		//  then
		verify(partyPostRepository).findByTitleContainingOrAddressContaining(anyString(), anyString(),
			any(Pageable.class));
		verify(partyPostValidator).filteringPosts(any(User.class), anyList());
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("모집글 검색 완료");
	}

	@Test
	void searchPartyPostByCategory() {
		//  given

		//  when
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(category.isActive()).thenReturn(true);
		when(partyPostRepository.findByCategoryIdAndActiveIsTrue(anyLong(), any(Pageable.class))).thenReturn(
			Collections.emptyList());
		when(partyPostValidator.filteringPosts(any(User.class), anyList())).thenReturn(Collections.emptyList());

		DataApiResponse<PartyPostListResponse> result = partyPostService.searchPartyPostByCategory(
			user, 999L, 99);
		//  then
		verify(categoryRepository).findById(anyLong());
		verify(category).isActive();
		verify(partyPostRepository).findByCategoryIdAndActiveIsTrue(anyLong(), any(Pageable.class));
		verify(partyPostValidator).filteringPosts(any(User.class), anyList());
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("카테고리별 모집글 조회 완료");
	}

	@Test
	void findHotPartyPost() {
		//  given
		Profile profile = mock(Profile.class);
		//  when
		when(partyPostRepository.findFirst3ByOrderByViewCntDesc(any(Pageable.class))).thenReturn(List.of(partyPost));

		DataApiResponse<PartyPostListResponse> result = partyPostService.findHotPartyPost(user);
		//  then
		verify(partyPostRepository).findFirst3ByOrderByViewCntDesc(any(Pageable.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("핫한 모집글 조회 완료");
		assertThat(result.getData().size()).isEqualTo(0);
	}

	@Test
	void findNearPartyPost() {
		//  given
		//  when
		when(partyPostRepository.findByAddressContaining(anyString(), any(Pageable.class)))
			.thenReturn(List.of(partyPost));
		when(partyPostValidator.filteringPosts(any(User.class), anyList())).thenReturn(List.of(partyPostListResponse));

		DataApiResponse<PartyPostListResponse> result = partyPostService.findNearPartyPost(user, "a b c");

		//  then
		verify(partyPostRepository).findByAddressContaining(anyString(), any(Pageable.class));
		verify(partyPostValidator).filteringPosts(any(User.class), anyList());
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("주변 모집글 조회 완료");
		assertThat(result.getData().size()).isEqualTo(1);
	}

	@Test
	void updatePartyPost() {
		//  given

		//  when
		when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
		when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
		when(partyPost.isWrittenByMe(anyLong())).thenReturn(true);

		ApiResponse result = partyPostService.updatePartyPost(partyPost.getId(), updatePartyPostRequest, user);
		//  then
		verify(partyPostRepository).findById(anyLong());
		verify(categoryRepository).findById(anyLong());
		verify(partyPost).isWrittenByMe(anyLong());
		verify(partyPost).update(any(UpdatePartyPostRequest.class), any(Category.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("모집글 수정 완료");
	}

	@Test
	void deletePartyPost() {
		//  given

		//  when
		when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
		doNothing().when(partyPostValidator).canDeletePartyPost(any(User.class), any(PartyPost.class));

		ApiResponse result = partyPostService.deletePartyPost(partyPost.getId(), user);
		//  then
		verify(partyPostRepository).findById(anyLong());
		verify(partyPostValidator).canDeletePartyPost(any(User.class), any(PartyPost.class));
		verify(partyPost).deletePartyPost();
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("모집글 삭제 완료");
	}

	@Test
	void findMyCreatedPartyList() {
		//  given

		//  when
		when(partyPostRepository.findByUserIdAndActiveIsTrue(anyLong(), any(Pageable.class))).thenReturn(
			List.of(partyPost));
		when(partyPost.getUser()).thenReturn(user);
		when(user.getId()).thenReturn(1L);
		when(partyPost.getStatus()).thenReturn(Status.FINDING);
		when(partyPost.getPartyDate()).thenReturn(LocalDateTime.now());
		when(partyPost.getCloseDate()).thenReturn(LocalDateTime.now());

		DataApiResponse<MyPartyPostListResponse> result = partyPostService.findMyCreatedPartyList(user, 99);
		//  then
		verify(partyPostRepository).findByUserIdAndActiveIsTrue(anyLong(), any(Pageable.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("내가 작성한 모집글 조회 완료");
		assertThat(result.getData().size()).isEqualTo(1);
	}

	@Test
	void findMyJoinedPartyList() {
		//  given
		Application application = mock(Application.class);
		User user2 = mock(User.class);
		//  when
		when(applicationRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(List.of(application));
		when(application.getUser()).thenReturn(user);
		when(partyPost.getUser()).thenReturn(user2);
		when(user2.getId()).thenReturn(2L);
		when(user.getId()).thenReturn(1L);
		when(application.getPartyPost()).thenReturn(partyPost);
		when(partyPost.getApplications()).thenReturn(List.of(application));
		when(partyPost.getStatus()).thenReturn(Status.FINDING);
		when(partyPost.getPartyDate()).thenReturn(LocalDateTime.now());
		when(partyPost.getCloseDate()).thenReturn(LocalDateTime.now());

		DataApiResponse<MyPartyPostListResponse> result = partyPostService.findMyJoinedPartyList(user, 99);
		//  then
		verify(applicationRepository).findByUserId(anyLong(), any(Pageable.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("내가 참가한 모집글 조회 완료");
		assertThat(result.getData().size()).isEqualTo(1);
	}

	@Test
	void toggleLikePartyPost() {
		//  given
		HashSet<PartyPost> set = new HashSet<>();
		set.add(partyPost);
		//  when
		when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(user.getLikePartyPosts()).thenReturn(set);

		ApiResponse result_on = partyPostService.toggleLikePartyPost(partyPost.getId(), user);
		//  then
		verify(partyPostRepository).findById(anyLong());
		verify(userRepository).save(any(User.class));
		assertThat(result_on.getCode()).isEqualTo(200);
		assertThat(result_on.getMsg()).isEqualTo("모집글 좋아요 취소 완료");

		when(user.getLikePartyPosts()).thenReturn(new HashSet<>());
		ApiResponse result_off = partyPostService.toggleLikePartyPost(partyPost.getId(), user);
		assertThat(result_off.getCode()).isEqualTo(200);
		assertThat(result_off.getMsg()).isEqualTo("모집글 좋아요 완료");
	}

	@Test
	void getLikePartyPost() {
		//  given
		Profile profile = mock(Profile.class);
		//  when
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(user.getLikePartyPosts()).thenReturn(Set.of(partyPost));
		when(partyPost.getUser()).thenReturn(user);
		when(user.getProfile()).thenReturn(profile);
		when(profile.getProfileImg()).thenReturn("testImg");
		when(user.getNickname()).thenReturn("nickname");
		when(partyPost.getStatus()).thenReturn(Status.FINDING);
		when(partyPost.getPartyDate()).thenReturn(LocalDateTime.now());
		when(partyPost.getCloseDate()).thenReturn(LocalDateTime.now());

		DataApiResponse<PartyPostListResponse> result = partyPostService.getLikePartyPost(user);
		//  then
		verify(userRepository).save(any(User.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("좋아요 게시글 조회 완료");
		assertThat(result.getData().size()).isEqualTo(1);
	}

	// throw Exception

	@Nested
	class Fail {
		@Test
		void createPartyPost_CategoryNotFoundException() {
			//  given
			Category category = mock(Category.class);
			//  when
			when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
			when(partyPostRequest.getPartyDate()).thenReturn("2023-02-16 12:00"); // 오류 뜰까?

			var thrown = assertThatThrownBy(
				() -> partyPostService.createPartyPost(user, partyPostRequest));
			//  then
			verify(userRepository).findById(anyLong());
			verify(categoryRepository).findById(anyLong());
			thrown.isInstanceOf(CategoryNotFoundException.class)
				.hasMessage(CategoryNotFoundException.MSG);
		}

		@Test
		void createPartyPost_CategoryNotActiveException() {
			//  given
			Category category = mock(Category.class);
			//  when
			when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
			when(partyPostRequest.getPartyDate()).thenReturn("2023-02-16 12:00"); // 오류 뜰까?
			when(partyPostRequest.getCategoryId()).thenReturn(999L);
			when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
			when(category.isActive()).thenReturn(false);

			var thrown = assertThatThrownBy(
				() -> partyPostService.createPartyPost(user, partyPostRequest));
			//  then
			verify(categoryRepository).findById(anyLong());
			verify(category).isActive();
			thrown.isInstanceOf(CategoryNotActiveException.class)
				.hasMessage(CategoryNotActiveException.MSG);
		}

		@Test
		void getPartyPost_PartyPostNotFoundException() {
			//  given
			//  when
			var thrown = assertThatThrownBy(
				() -> partyPostService.getPartyPost(partyPost.getId(), user));
			//  then
			verify(partyPostRepository).findByIdAndActiveIsTrue(anyLong());
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessage(PartyPostNotFoundException.MSG);
		}

		@Test
		void searchPartyPostByCategory_CategoryNotFoundException() {
			//  given

			//  when

			var thrown = assertThatThrownBy(
				() -> partyPostService.searchPartyPostByCategory(user, category.getId(), 999));
			//  then
			verify(categoryRepository).findById(anyLong());
			thrown.isInstanceOf(CategoryNotFoundException.class)
				.hasMessage(CategoryNotFoundException.MSG);
		}

		@Test
		void searchPartyPostByCategory_CategoryNotActiveException() {
			//  given

			//  when
			when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

			var thrown = assertThatThrownBy(
				() -> partyPostService.searchPartyPostByCategory(user, category.getId(), 999));
			//  then
			verify(categoryRepository).findById(anyLong());
			verify(category).isActive();
			thrown.isInstanceOf(CategoryNotActiveException.class)
				.hasMessage(CategoryNotActiveException.MSG);
		}

		@Test
		void updatePartyPost_PartyPostNotFoundException() {
			//  given

			//  when

			var thrown = assertThatThrownBy(
				() -> partyPostService.updatePartyPost(partyPost.getId(), updatePartyPostRequest, user));
			//  then
			verify(partyPostRepository).findById(anyLong());
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessage(PartyPostNotFoundException.MSG);
		}

		@Test
		void updatePartyPost_CategoryNotFoundException() {
			//  given

			//  when
			when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));

			var thrown = assertThatThrownBy(
				() -> partyPostService.updatePartyPost(partyPost.getId(), updatePartyPostRequest, user));
			//  then
			verify(partyPostRepository).findById(anyLong());
			verify(categoryRepository).findById(anyLong());
			thrown.isInstanceOf(CategoryNotFoundException.class)
				.hasMessage(CategoryNotFoundException.MSG);
		}

		@Test
		void updatePartyPost_IsNotWritterException() {
			//  given

			//  when
			when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
			when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

			var thrown = assertThatThrownBy(
				() -> partyPostService.updatePartyPost(partyPost.getId(), updatePartyPostRequest, user));
			//  then
			verify(partyPostRepository).findById(anyLong());
			verify(categoryRepository).findById(anyLong());
			verify(partyPost).isWrittenByMe(anyLong());
			thrown.isInstanceOf(IsNotWritterException.class)
				.hasMessage(IsNotWritterException.MSG);
		}

		@Test
		void deletePartyPost() {
			//  given

			//  when

			var thrown = assertThatThrownBy(
				() -> partyPostService.deletePartyPost(partyPost.getId(), user));
			//  then
			verify(partyPostRepository).findById(anyLong());
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessage(PartyPostNotFoundException.MSG);
		}

		@Test
		void toggleLikePartyPost_PartyPostNotFoundException() {
			//  given
			HashSet<PartyPost> set = new HashSet<>();
			set.add(partyPost);
			//  when

			var thrown = assertThatThrownBy(
				() -> partyPostService.toggleLikePartyPost(partyPost.getId(), user));
			//  then
			verify(partyPostRepository).findById(anyLong());
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessage(PartyPostNotFoundException.MSG);
		}
	}
}