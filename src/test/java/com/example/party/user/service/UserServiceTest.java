package com.example.party.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.party.global.dto.ResponseDto;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.Profile;
import com.example.party.user.entity.User;
import com.example.party.user.repository.ProfileRepository;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	//mock은 가짜개체
	@InjectMocks //mock을 이용한다.
	private UserService userService;

	@Mock
	private ProfileRepository profileRepository;

	@Spy
	private BCryptPasswordEncoder passwordEncoder;

	@Test
	@DisplayName("회원가입")
	void 회원가입_성공() {
		// given
		SignupRequest request = SignupRequest.builder()
			.email("asd123@gmail.com")
			.password("asd123!@#")
			.nickname("김김김")
			.phoneNum("123-1234-1234")
			.build();

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn(Optional.empty());

		// when
		ResponseDto response = userService.signUp(request);

		// then
		assertThat(response.getCode()).isEqualTo(201);
		assertThat(response.getMsg()).isEqualTo("회원가입 완료");

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("회원가입")
	void 중복된_이메일_실패() {
		// given
		SignupRequest request = SignupRequest.builder()
			.email("asd123@gmail.com")
			.password("asd123!@#")
			.nickname("김김김")
			.phoneNum("123-1234-1234")
			.build();

		String email = "asd123@gmail.com";
		String password = "asd123!@#";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";
		User user = new User(email, password, nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.of(user)));

		// when
		ResponseDto response = userService.signUp(request);

		// then
		assertThat(response.getCode()).isEqualTo(201);
		assertThat(response.getMsg()).isEqualTo("회원가입 완료");

		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("로그인")
	void 로그인_성공() {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("asd123@gmail.com")
			.password("asd123!@#")
			.build();

		String email = "asd123@gmail.com";
		String password = "asd123!@#";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";
		User user = new User(email, passwordEncoder.encode(password), nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.of(user)));

		// when
		ResponseDto response = userService.signIn(request, servletResponse);

		// then
		assertThat(response.getCode()).isEqualTo(200);
		assertThat(response.getMsg()).isEqualTo("로그인 완료");
		assertThat(Objects.requireNonNull(servletResponse.getHeaderValue("Authorization")).toString()).isNotEmpty();
	}

	@Test
	@DisplayName("로그인")
	void 입력값_다름_실패() {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("asd123@gmail.com")
			.password("asd123!@#")
			.build();

		String email = "asd123@gmail.com";
		String password = "asd123!";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";
		User user = new User(email, passwordEncoder.encode(password), nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.of(user)));

		// when
		ResponseDto response = userService.signIn(request, servletResponse);

		// then
		assertThat(response.getCode()).isEqualTo(200);
		assertThat(response.getMsg()).isEqualTo("로그인 완료");
		assertThat(Objects.requireNonNull(servletResponse.getHeaderValue("Authorization")).toString()).isNotEmpty();
	}

	@Test
	@DisplayName("로그인")
	void 레포짓토리_이메일_없음() {
		// given
		LoginRequest request = LoginRequest.builder()
			.email("asd123@gmail.com")
			.password("asd123!@#")
			.build();

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.empty()));

		// when
		ResponseDto response = userService.signIn(request, servletResponse);

		// then
		assertThat(response.getCode()).isEqualTo(200);
		assertThat(response.getMsg()).isEqualTo("로그인 완료");
		assertThat(Objects.requireNonNull(servletResponse.getHeaderValue("Authorization")).toString()).isNotEmpty();
	}

	@Test
	@DisplayName("로그아웃")
	void 로그아웃() {

	}

	@Test
	@DisplayName("회원탈퇴")
	void 회원탈퇴_성공() {
		// given
		WithdrawRequest request = WithdrawRequest.builder()
			.password("asd123!@#")
			.build();

		String email = "asd123@gmail.com";
		String password = "asd123!@#";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";
		User userDetails = new User(email, passwordEncoder.encode(password), nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.of(userDetails)));

		// when
		ResponseDto response = userService.withdraw(userDetails, request);

		// then
		assertThat(response.getCode()).isEqualTo(200);
		assertThat(response.getMsg()).isEqualTo("로그인 완료");
	}

	@Test
	@DisplayName("회원탈퇴")
	void 비밀번호_비일치() {
		// given
		WithdrawRequest request = WithdrawRequest.builder()
			.password("asd12!")
			.build();

		String email = "asd123@gmail.com";
		String password = "asd123!@#";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";
		User userDetails = new User(email, passwordEncoder.encode(password), nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);

		userRepository.save(userDetails);

		when(userRepository.findByEmail(any(String.class)))
			.thenReturn((Optional.of(userDetails)));

		// when
		ResponseDto response = userService.withdraw(userDetails, request);

		// then
		assertThat(response.getCode()).isEqualTo(200);
		assertThat(response.getMsg()).isEqualTo("로그인 완료");

	}

	@Test
	@DisplayName("프로필 수정")
	void updateProfile() {

		// given
		String email = "asd123@gmail.com";
		String password = "asd123!@#";
		String nickname = "ㅁㄴㅇ";
		String phoneNum = "123-1234-1234";

		User user = new User(email, passwordEncoder.encode(password), nickname, phoneNum
			, UserRole.ROLE_USER, Status.ACTIVE);
		user.setId(1l);
		Long id = user.getId();

		String proFileUrl = "0ㄱㄱㄷㄱ0";
		String comment = "프로필 내용을 수정합니다.";

		Profile profile = new Profile(proFileUrl, comment, 5, 5);
		profile.setId(id);

		//수정할 수정사항
		ProfileRequest profileRequest = ProfileRequest.builder()
			.nickName("김홍길동")
			.phoneNum("010-1234-4321")
			.proFileUrl("0ㄱㄱㄷㄱ0")
			.comment("프로필 내용을 수정합니다.")
			.build();

		// when
		ResponseDto responseDto = userService.updateProfile(profileRequest, 0l);

		//then 검증
		assertThat(responseDto.getCode()).isEqualTo(200);
		assertThat(responseDto.getMsg()).isEqualTo("프로필 정보 수정 완료");
		// assertThat(responseDto.getMsg().getNickName()).isEqualTo("test");
		// assertThat(responseDto.getMsg().getPhoneNum()).isEqualTo("01012345678");
		// assertThat(responseDto.getMsg().getProFileUrl()).isEqualTo("test.com");
		// assertThat(responseDto.getMsg().getComment()).isEqualTo("test comment");

	}

	@Test
	void getMyProfile() {
	}

	@Test
	void getOtherProfile() {
	}
}