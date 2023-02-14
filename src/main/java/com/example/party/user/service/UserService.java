package com.example.party.user.service;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.dto.LoginRequest;

import com.example.party.user.dto.ProfileResponse;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;
import com.example.party.util.JwtProvider;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
public class UserService implements IUserService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;


	private User findByUser(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일"));
	}

	private void confirmPassword(String requestPassword, String savedPassword) {
		if(!passwordEncoder.matches(requestPassword, savedPassword))
		{ throw new IllegalArgumentException("비밀번호 불일치"); }
	}

	@Override
	public ResponseDto signUp(SignupRequest signupRequest) {
		Optional<User> users = userRepository.findByEmail(signupRequest.getEmail());
		if (users.isPresent()) { throw new IllegalArgumentException("유저가 존재합니다"); }

		String userEmail = signupRequest.getEmail();
		String password = passwordEncoder.encode(signupRequest.getPassword());
		User user = new User(userEmail, password, signupRequest.getNickname(), signupRequest.getPhoneNum()
			, UserRole.ROLE_USER, Status.ACTIVE);
		userRepository.save(user);
		return new ResponseDto(201, "회원가입 완료");
	}

	@Override
	public ResponseDto signIn(LoginRequest loginRequest, HttpServletResponse response) {
		User user = findByUser(loginRequest.getEmail());
		confirmPassword(loginRequest.getPassword(), user.getPassword());
		String generateToken = JwtProvider.generateToken(user);
		response.addHeader(jwtProvider.AUTHORIZATION_HEADER, generateToken);
		return new ResponseDto(200, "로그인 완료");

	@Override
	public ResponseDto withdraw(User userDetails, WithdrawRequest withdrawRequest) {
		User user = findByUser(userDetails.getEmail());
		confirmPassword(withdrawRequest.getPassword(), user.getPassword());
		user.changeDORMANT();
		return new ResponseDto(200, "회원탈퇴 완료");
	}

	public DataResponseDto<MyProfileResponse> updateProfile(ProfileRequest profileRequest, Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저 찾기 실패")); //user 정보 조회
		user.updataProfile(profileRequest.getNickName(), profileRequest.getPhoneNum()); //user 정보 수정

		Long profileId = user.getProfile().getId(); // 유저 정보를 이용한 프로필 id 찾기
		Profile profile = profileRepository.findById(profileId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 찾기 실패")); // 프로필 정보 조회
		profile.updateProfile(profileRequest.getProFileUrl(), profileRequest.getComment()); // 프로필 정보 수정
		MyProfileResponse myProfileResponse = new MyProfileResponse(user, profile); // profile 내용 입력

		return new DataResponseDto(200, "프로필 정보 수정 완료", myProfileResponse);

	}

	public DataResponseDto<MyProfileResponse> getMyProfile(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저 찾기 실패")); //user 정보 조회

		Long profileId = user.getProfile().getId(); // 유저 정보를 이용한 프로필 id 찾기
		Profile profile = profileRepository.findById(profileId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 찾기 실패")); // 프로필 정보 조회
		MyProfileResponse myProfileResponse = new MyProfileResponse(user, profile); // profile 내용 입력

		return new DataResponseDto(200, "프로필 조회", myProfileResponse);
	}

	public DataResponseDto<MyProfileResponse> getOtherProfile(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저 찾기 실패")); //user 정보 조회
		
		Long profileId = user.getProfile().getId(); // 유저 정보를 이용한 프로필 id 찾기
		Profile profile = profileRepository.findById(profileId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로필 찾기 실패")); // 프로필 정보 조회
		OtherProfileResponse otherProfileResponse = new OtherProfileResponse(user, profile); // profile 내용 입력

		return new DataResponseDto(200, "프로필 조회", otherProfileResponse);
	}
}