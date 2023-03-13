package com.example.party.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.application.entity.Application;
import com.example.party.application.exception.ApplicationNotFoundException;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.exception.ForbiddenException;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;

// TODO: 2023/02/26
// 주최자와 신청참가자로 테스트 출력을 나누는게 좋을 듯.
// 테스트명 naming convention 찾아보기 should, test
@ExtendWith({MockitoExtension.class})
class ApplicationServiceTest {

	@Mock
	private ApplicationRepository applicationRepository;
	@Mock
	private PartyPostRepository partyPostRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ApplicationValidator applicationValidator;
	@InjectMocks
	private ApplicationService applicationService;

	private Application application;
	private User user;
	private PartyPost partyPost;

	@BeforeEach
	public void setUp() {
		this.application = mock(Application.class);
		this.user = mock(User.class);
		this.partyPost = mock(PartyPost.class);
	}

	@DisplayName("성공")
	@Nested
	class Success {
		@Test
		@DisplayName("모집글에 참가신청을 한다")
		void createApplication() {
			//  given

			//  when
			when(userRepository.save(any(User.class))).thenReturn(user);
			when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
			doNothing().when(applicationValidator)
				.validationApplicationBeforeCreation(any(PartyPost.class), any(User.class));
			when(applicationRepository.save(any(Application.class))).thenReturn(application);

			ApiResponse result = applicationService.createApplication(partyPost.getId(), user);
			//  then
			verify(userRepository).save(any(User.class));
			verify(partyPostRepository).findById(anyLong());
			verify(applicationValidator).validationApplicationBeforeCreation(any(PartyPost.class), any(User.class));
			verify(applicationRepository).save(any(Application.class));
			verify(partyPost).addApplication(any(Application.class));
			verify(user).addApplication(any(Application.class));
			assertThat(result.getCode()).isEqualTo(200);
			assertThat(result.getMsg()).isEqualTo("참가 신청 완료");
		}

		@Test
		@DisplayName("모집글에 지원한 참가신청을 취소 한다")
		void cancelApplication() {
			//  given
			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
			when(application.isWrittenByMe(anyLong())).thenReturn(true);

			ApiResponse result = applicationService.cancelApplication(application.getId(), user);

			//  then
			verify(applicationRepository).findById(anyLong());
			verify(application).isWrittenByMe(anyLong());
			verify(application).cancel();
			assertThat(result.getCode()).isEqualTo(200);
			assertThat(result.getMsg()).isEqualTo("참가 신청 취소 완료");
		}

		@Test
		@DisplayName("내가 작성한 모집글에 신청한 참가신청서들을 조회 한다")
		void getApplications() {
			//  given
			Page<Application> applications = mock(Page.class);

			//  when
			when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));
			when(partyPost.isWrittenByMe(anyLong())).thenReturn(true);
			when(applicationRepository.findAllByPartyPostAndCancelIsFalse(any(PartyPost.class), any(Pageable.class)))
				.thenReturn(applications);

			DataApiResponse<ApplicationResponse> result = applicationService.getApplications(partyPost.getId(),
				user);

			//  then
			verify(partyPostRepository).findById(anyLong());
			verify(partyPost).isWrittenByMe(anyLong());
			verify(applicationRepository).findAllByPartyPostAndCancelIsFalse(any(PartyPost.class), any(Pageable.class));
			assertThat(result.getCode()).isEqualTo(200);
			assertThat(result.getMsg()).isEqualTo("참가신청자 목록 조회 완료");
			assertThat(result.getData()).isEmpty();
		}

		@Test
		@DisplayName("내가 작성한 모집글에 신청한 하나의 참가신청서를 참가수락한다")
		void acceptApplication() {
			//  given

			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
			when(application.isSendToMe(anyLong())).thenReturn(true);
			doNothing().when(applicationValidator).validateApplicationStatus(any(Application.class));

			ApiResponse result = applicationService.acceptApplication(application.getId(), user);

			//  then
			verify(applicationRepository).findById(anyLong());
			verify(application).accept();
			assertThat(result.getCode()).isEqualTo(200);
			assertThat(result.getMsg()).isEqualTo("참가 신청 수락 완료");
		}

		@Test
		@DisplayName("내가 작성한 모집글에 신청한 하나의 참가신청서를 참가거부한다")
		void rejectApplication() {
			//  given

			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
			when(application.isSendToMe(anyLong())).thenReturn(true);
			doNothing().when(applicationValidator).validateApplicationStatus(any(Application.class));

			ApiResponse result = applicationService.rejectApplication(application.getId(), user);

			//  then
			verify(applicationRepository).findById(anyLong());
			verify(application).isSendToMe(anyLong());
			verify(application).reject();
			assertThat(result.getCode()).isEqualTo(200);
			assertThat(result.getMsg()).isEqualTo("참가 신청 거부 완료");
		}

		@Test
		@DisplayName("내가 작성한 모집글에 신청한 하나의 참가신청서를 조회한다")
		void getApplication() {
			//  given

			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));

			applicationService.getApplication(999L);
			//  then
			verify(applicationRepository).findById(anyLong());
		}
	}

	@DisplayName("실패")
	@Nested
	class Fail {
		@Test
		void createApplication_PartyPostNotFoundException() {
		//  given

		//  when
			when(userRepository.save(any(User.class))).thenReturn(user);

			var thrown = assertThatThrownBy(() ->
				applicationService.createApplication(partyPost.getId(), user)
			);
			//  then
			verify(userRepository).save(any(User.class));
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessageContaining(PartyPostNotFoundException.MSG);
		}

		@Test
		@DisplayName("내가 신청하지 않은 신청서는 취소할 수 없다")
		void cancelApplication_FORBIDDEN() {
			//  given
			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
			when(application.isWrittenByMe(anyLong())).thenReturn(false);

			var thrown = assertThatThrownBy(() -> {
				applicationService.cancelApplication(application.getId(), user);
			});

			//  then
			thrown.isInstanceOf(ForbiddenException.class)
				.hasMessageContaining("허가되지 않은 요청입니다.");
			verify(applicationRepository).findById(anyLong());
		}

		@Test
		void getApplications_PartyPostNotFoundException() {
			//  given

			//  when
			var thrown = assertThatThrownBy(() -> {
				applicationService.getApplications(partyPost.getId(), user);
			});

			//  then
			thrown.isInstanceOf(PartyPostNotFoundException.class)
				.hasMessageContaining( "존재하지 않는 모집글 입니다.");
			verify(partyPostRepository).findById(anyLong());
		}

		@Test
		void getApplications_FORBIDDEN() {
			//  given
			//  when
			when(partyPostRepository.findById(anyLong())).thenReturn(Optional.of(partyPost));

			var thrown = assertThatThrownBy(() -> {
				applicationService.getApplications(partyPost.getId(), user);
			});

			//  then
			thrown.isInstanceOf(ForbiddenException.class)
				.hasMessageContaining("허가되지 않은 요청입니다.");
			verify(partyPostRepository).findById(anyLong());
			verify(partyPost).isWrittenByMe(anyLong());
		}

		@Test
		void acceptApplication_FORBIDDEN() {
			//  given
			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));

			var thrown = assertThatThrownBy(() -> {
				applicationService.acceptApplication(partyPost.getId(), user);
			});

			//  then
			thrown.isInstanceOf(ForbiddenException.class)
				.hasMessageContaining("허가되지 않은 요청입니다.");
			verify(applicationRepository).findById(anyLong());
			verify(application).isSendToMe(anyLong());
		}

		@Test
		void rejectApplication_FORBIDDEN() {
			//  given
			//  when
			when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));

			var thrown = assertThatThrownBy(() -> {
				applicationService.rejectApplication(application.getId(), user);
			});

			//  then
			thrown.isInstanceOf(ForbiddenException.class)
				.hasMessageContaining("허가되지 않은 요청입니다.");
			verify(applicationRepository).findById(anyLong());
		}

		@Test
		void getApplication_ApplicationNotFoundException() {
		//  given
		//  when
			var thrown = assertThatThrownBy(() -> {
				applicationService.getApplication(anyLong());
			});
			//  then

			thrown.isInstanceOf(ApplicationNotFoundException.class)
				.hasMessageContaining(ApplicationNotFoundException.MSG);
			verify(applicationRepository).findById(anyLong());
		}

	}

}