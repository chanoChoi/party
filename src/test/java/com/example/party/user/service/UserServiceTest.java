package com.example.party.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.example.party.global.common.ApiResponse;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.Profile;
import com.example.party.user.entity.User;
import com.example.party.user.exception.EmailOverlapException;
import com.example.party.user.exception.ExistNicknameException;
import com.example.party.user.repository.ProfilesRepository;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ProfilesRepository profilesRepository;
	@InjectMocks
	private UserService userService;
	private User user;
	private ValueOperations<String, String> valueOperations;
	private LoginRequest loginRequest;

	@BeforeEach
	public void setup() {
		this.user = mock(User.class);
		this.valueOperations = mock(ValueOperations.class);
		this.loginRequest = mock(LoginRequest.class);
	}

	@Test
	void signUp() {
		//  given
		SignupRequest signupRequest = mock(SignupRequest.class);
		//  when
		when(signupRequest.getEmail()).thenReturn("email@test.com");
		when(signupRequest.getNickname()).thenReturn("NICKNAME");
		when(signupRequest.getPassword()).thenReturn("password1!");
		when(userRepository.existsUserByEmail(anyString())).thenReturn(false);
		when(userRepository.existsUserByNickname(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASSWORD");

		ApiResponse result = userService.signUp(signupRequest);
		//  then
		verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		verify(passwordEncoder).encode(anyString());
		verify(userRepository).save(any(User.class));
		assertThat(result.getCode()).isEqualTo(201);
		assertThat(result.getMsg()).isEqualTo("회원가입 완료");
	}

	@Test
	void signIn() {
		//  given

		//  when
		when(loginRequest.getEmail()).thenReturn("EMAIL");
		when(loginRequest.getPassword()).thenReturn("test1234!");
		when(user.getPassword()).thenReturn("test1234!");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(anyString())).thenReturn(null);
		when(user.getStatus()).thenReturn(Status.ACTIVE);
		String result = userService.signIn(loginRequest);
		//  then
		verify(userRepository).findByEmail(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(redisTemplate).opsForValue();
		verify(valueOperations).get(anyString());
		verify(valueOperations).set(anyString(), anyString());
		assertThat(result).isNotBlank();
	}

	@Test
	void signOut() {
		//  given
		RedisOperations<String, String> operations = mock(RedisOperations.class);

		//  when
		when(user.getId()).thenReturn(1L);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getOperations()).thenReturn(operations);
		when(operations.delete(anyString())).thenReturn(true);
		ApiResponse result = userService.signOut(user);
		//  then
		verify(operations).delete(anyString());
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("로그아웃 완료");
	}

	@Test
	void withdraw() {
		//  given
		WithdrawRequest withdrawRequest = mock(WithdrawRequest.class);
		RedisOperations operations = mock(RedisOperations.class);
		//  when
		when(user.getEmail()).thenReturn("EMAIL");
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(user.getPassword()).thenReturn("password1!");
		when(withdrawRequest.getPassword()).thenReturn("password1!");
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getOperations()).thenReturn(operations);
		when(operations.delete(anyString())).thenReturn(true);
		when(user.getId()).thenReturn(1L);

		ApiResponse result = userService.withdraw(user, withdrawRequest);
		//  then
		verify(userRepository).findByEmail(anyString());
		verify(passwordEncoder).matches(anyString(), anyString());
		verify(user).DormantState();
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("회원탈퇴 완료");
	}

	@Test
	void updateProfile() throws IOException {
		//  given
		ProfileRequest profileRequest = mock(ProfileRequest.class);
		Profile profile = mock(Profile.class);
		MultipartFile file = mock(MultipartFile.class);
		//  when
		when(profileRequest.checkingInput(user)).thenReturn(profileRequest);
		when(profileRequest.getNickname()).thenReturn("nickname");
		when(user.getNickname()).thenReturn("nickname");
		when(user.getProfile()).thenReturn(profile);
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(profileRequest.getProfileImg()).thenReturn("profileImg");
		when(profileRequest.getComment()).thenReturn("comment");
		doNothing().when(user).updateProfile(any(ProfileRequest.class));
		when(file.isEmpty()).thenReturn(true);

		ApiResponse result = userService.updateProfile(user, profileRequest, file);
		//  then
		verify(userRepository).save(any(User.class));
		verify(userRepository, times(0)).existsUserByNickname(profileRequest.getNickname());
		verify(user).updateProfile(any(ProfileRequest.class));
		verify(profilesRepository).save(any(Profile.class));
		verify(userRepository).save(any(User.class));
		assertThat(result.getCode()).isEqualTo(200);
		assertThat(result.getMsg()).isEqualTo("프로필 정보 수정 완료");
	}

	@Test
	void signUp_EmailOverlapException() {
		//  given
		SignupRequest signupRequest = mock(SignupRequest.class);
		//  when
		when(signupRequest.getEmail()).thenReturn("email@test.com");
		when(userRepository.existsUserByEmail(anyString())).thenReturn(true);

		var thrown = assertThatThrownBy(
			() -> userService.signUp(signupRequest));
		//  then
		verify(userRepository).existsUserByEmail(anyString());
		thrown.isInstanceOf(EmailOverlapException.class)
			.hasMessage(EmailOverlapException.MSG);
	}

	@Test
	void signUp_ExistNicknameException() {
		//  given
		SignupRequest signupRequest = mock(SignupRequest.class);
		//  when
		when(signupRequest.getEmail()).thenReturn("email@test.com");
		when(signupRequest.getNickname()).thenReturn("NICKNAME");
		when(userRepository.existsUserByNickname(anyString())).thenReturn(true);

		var thrown = assertThatThrownBy(
			() -> userService.signUp(signupRequest));
		//  then
		// verify(userRepository).existsUserByEmail(anyString());
		verify(userRepository).existsUserByNickname(anyString());
		thrown.isInstanceOf(ExistNicknameException.class)
			.hasMessage(ExistNicknameException.MSG);
	}
}