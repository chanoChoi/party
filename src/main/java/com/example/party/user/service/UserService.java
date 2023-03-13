package com.example.party.user.service;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.ItemApiResponse;
import com.example.party.global.exception.LoginException;
import com.example.party.global.util.JwtProvider;
import com.example.party.global.util.S3Uploader;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.MyProfileResponse;
import com.example.party.user.dto.OtherProfileResponse;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.Profile;
import com.example.party.user.entity.User;
import com.example.party.user.exception.EmailOverlapException;
import com.example.party.user.exception.ExistNicknameException;
import com.example.party.user.exception.UserNotFoundException;
import com.example.party.user.repository.ProfilesRepository;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService implements IUserService {

	private static final String RT_TOKEN = "rTKey";
	private final UserRepository userRepository;
	private final ProfilesRepository profilesRepository;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, String> redisTemplate;
	private final S3Uploader s3Uploader;

	//회원가입
	@Override
	public ApiResponse signUp(SignupRequest signupRequest) {
		if (userRepository.existsUserByEmail(signupRequest.getEmail())) {
			throw new EmailOverlapException();
		}

		if (userRepository.existsUserByNickname(signupRequest.getNickname())) {
			throw new ExistNicknameException();
		}

		String password = passwordEncoder.encode(signupRequest.getPassword());

		User user = new User(signupRequest, password);
		userRepository.save(user);
		return ApiResponse.create("회원가입 완료");
	}

	//로그인
	@Override
	public String signIn(LoginRequest loginRequest) {
		User user = findByUser(loginRequest.getEmail());
		confirmPassword(loginRequest.getPassword(), user.getPassword());
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		if (valueOperations.get(RT_TOKEN + user.getId()) != null) { // JwtVerificationFilter 45번째 줄
			redisTemplate.delete(RT_TOKEN + user.getId());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 로그인");
		}
		if (user.getStatus().equals(Status.DORMANT)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "탈퇴한 사용자 입니다");
		}

		String generateToken = JwtProvider.accessToken(user.getEmail(), user.getId());
		String refreshToken = JwtProvider.refreshToken(user.getEmail(), user.getId());

		valueOperations.set(RT_TOKEN + user.getId(), refreshToken);
		return generateToken + "," + refreshToken;
	}

	//로그아웃
	@Override
	public ApiResponse signOut(User user) {
		// 이후 security 에 url 보안 설정 필요합니다.
		redisTemplate.opsForValue().getOperations().delete(RT_TOKEN + user.getId());
		return ApiResponse.ok("로그아웃 완료");
	}

	//회원탈퇴
	@Override
	public ApiResponse withdraw(User user, WithdrawRequest withdrawRequest) {
		User userIf = findByUser(user.getEmail());
		confirmPassword(withdrawRequest.getPassword(), userIf.getPassword());
		userIf.DormantState();
		redisTemplate.opsForValue().getOperations().delete(RT_TOKEN + user.getId());
		return ApiResponse.ok("회원탈퇴 완료");
	}

	//프로필 정보 수정
	@Override
	public ApiResponse updateProfile(User user, ProfileRequest profileRequest, MultipartFile file)
		throws IOException {
		ProfileRequest uniqueInput = profileRequest.checkingInput(user);
		if (!uniqueInput.getNickname().equals(user.getNickname())) {
			if (userRepository.existsUserByNickname(profileRequest.getNickname())) {
				throw new ExistNicknameException();
			}
		}

		Profile profile = user.getProfile();
		profile.updateProfile(uniqueInput.getProfileImg(), uniqueInput.getComment());
		user.updateProfile(uniqueInput);

		if (!file.isEmpty()) {
			String storedFileName = s3Uploader.upload(file, "static");
			profile.setProfileImg(storedFileName);
		}

		profilesRepository.save(profile);
		userRepository.save(user); //변경한 user 저장
		return ApiResponse.ok("프로필 정보 수정 완료"); //결과값 반환
	}

	//내 프로필 조회
	public ApiResponse getMyProfile(User user) {
		MyProfileResponse myProfileResponse = new MyProfileResponse(user); // profile 내용 입력
		return ItemApiResponse.ok("내 프로필 조회", myProfileResponse); //결과값 반환
	}

	//상대방 프로필 조회
	public ApiResponse getOtherProfile(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(UserNotFoundException::new); //user 정보 조회
		OtherProfileResponse otherProfileResponse = new OtherProfileResponse(user); // profile 내용 입력
		return ItemApiResponse.ok("타 프로필 조회", otherProfileResponse); //결과값 반환
	}

	//private 메소드
	//repository 에서 user 찾기
	private User findByUser(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(UserNotFoundException::new);
	}

	//비밀번호 확인
	private void confirmPassword(String requestPassword, String savedPassword) {
		if (!passwordEncoder.matches(requestPassword, savedPassword)) {
			throw new LoginException();
		}
	}
}
