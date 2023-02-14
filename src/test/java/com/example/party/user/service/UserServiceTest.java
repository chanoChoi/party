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
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

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
}