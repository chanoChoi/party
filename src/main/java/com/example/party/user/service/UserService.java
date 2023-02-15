package com.example.party.user.service;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.MyProfileResponse;
import com.example.party.user.dto.OtherProfileResponse;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.repository.ProfileRepository;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;
import com.example.party.util.JwtProvider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	//회원가입
	@Override
	public ResponseDto signUp(SignupRequest signupRequest) {
		Optional<User> users = userRepository.findByEmail(signupRequest.getEmail());
		if (users.isPresent()) {
			throw new IllegalArgumentException("유저가 존재합니다");
		}

		String userEmail = signupRequest.getEmail();
		String password = passwordEncoder.encode(signupRequest.getPassword());
		User user = new User(userEmail, password, signupRequest.getNickname(), signupRequest.getPhoneNum(),
			UserRole.ROLE_USER, Status.ACTIVE);
		userRepository.save(user);
		return new ResponseDto(201, "회원가입 완료");
	}

	//로그인
	@Override
	public ResponseDto signIn(LoginRequest loginRequest, HttpServletResponse response) {
		User user = findByUser(loginRequest.getEmail());
		confirmPassword(loginRequest.getPassword(), user.getPassword());
		String generateToken = JwtProvider.generateToken(user);
		response.addHeader(jwtProvider.AUTHORIZATION_HEADER, generateToken);
		return new ResponseDto(200, "로그인 완료");
	}

	@Override
	public ResponseDto signOut(User user) {
		return null;
	}

	//회원탈퇴
	@Override
	public ResponseDto withdraw(User userDetails, WithdrawRequest withdrawRequest) {
		User user = findByUser(userDetails.getEmail());
		confirmPassword(withdrawRequest.getPassword(), user.getPassword());
		user.changeDORMANT();
		return new ResponseDto(200, "회원탈퇴 완료");
	}

	//프로필 수정
	public DataResponseDto<MyProfileResponse> updateProfile(ProfileRequest profileRequest, Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저 찾기 실패")); //user 정보 조회
		user.updataProfile(profileRequest); //user 정보 수정
		MyProfileResponse myProfileResponse = new MyProfileResponse(user); // profile 내용 입력
		return new DataResponseDto(200, "프로필 정보 수정 완료", myProfileResponse);

	}

	//내 프로필 조회
	public DataResponseDto<MyProfileResponse> getMyProfile(User user) {
		MyProfileResponse myProfileResponse = new MyProfileResponse(user); // profile 내용 입력
		return new DataResponseDto(200, "내 프로필 조회", myProfileResponse);
	}

	//상대방 프로필 조회
	public DataResponseDto<MyProfileResponse> getOtherProfile(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저 찾기 실패")); //user 정보 조회
		OtherProfileResponse otherProfileResponse = new OtherProfileResponse(user); // profile 내용 입력
		return new DataResponseDto(200, "타 프로필 조회", otherProfileResponse);
	}

	//private 메소드
	//repository에서 user 찾기
	private User findByUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일"));
	}

	//비밀번호 확인
	private void confirmPassword(String requestPassword, String savedPassword) {
		if (!passwordEncoder.matches(requestPassword, savedPassword)) {
			throw new IllegalArgumentException("비밀번호 불일치");
		}
	}

}
