package com.example.party.user.service;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.global.util.JwtProvider;
import com.example.party.user.dto.KakaoUserInfoDto;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;
import com.example.party.user.type.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
	private static final String RT_TOKEN = "rTKey";
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public String kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
		// 나의 카카오 인가코드로 카카오 측에 엑세스 토큰 요청
		String accessToken = getToken(code);
		// 카카오 접속 엑세스 토큰으로 카카오 api 호출 : json 형태 사용자 정보 가져오기
		KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
		// 검증 로직 (필요시 회원가입 포함)
		User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);

		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		if (valueOperations.get(RT_TOKEN + kakaoUser.getId()) != null) { // JwtVerificationFilter 45번째 줄
			redisTemplate.delete(RT_TOKEN + kakaoUser.getId());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 로그인");
		}

		if (kakaoUser.getStatus().equals(Status.DORMANT)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "탈퇴한 사용자 입니다");
		}

		//JWT 토큰 반환
		String generateToken = JwtProvider.accessToken(kakaoUser.getEmail(), kakaoUser.getId());
		String refreshToken = JwtProvider.refreshToken(kakaoUser.getEmail(), kakaoUser.getId());

		valueOperations.set(RT_TOKEN + kakaoUser.getId(), refreshToken);
		return generateToken + "," + refreshToken;

	}

	// 1. 나의 인가 코드 로 "카카오에 액세스 토큰" 요청
	private String getToken(String code) throws JsonProcessingException {
		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HTTP Body 생성
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "94293d41f51b73a7ba87db77967da2b3");
		body.add("redirect_uri", "http://13.124.4.58:8080/api/users/kakao/callback");
		body.add("code", code);

		// HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
			new HttpEntity<>(body, headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange(
			"https://kauth.kakao.com/oauth/token",
			HttpMethod.POST,
			kakaoTokenRequest,
			String.class
		);

		// HTTP 응답 (JSON) -> 액세스 토큰 파싱
		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		return jsonNode.get("access_token").asText();
	}

	// 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
	private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
		// HTTP Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/json");

		// HTTP 요청 보내기
		HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> response = rt.exchange(
			"https://kapi.kakao.com/v2/user/me",
			HttpMethod.POST,
			kakaoUserInfoRequest,
			String.class
		);

		String responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		Long id = jsonNode.get("id").asLong();
		String nickname = jsonNode.get("properties")
			.get("nickname").asText();
		String email = jsonNode.get("kakao_account")
			.get("email").asText();

		return new KakaoUserInfoDto(id, nickname, email);
	}

	// 3. 카카오에서 받아온 유저정보 검증, 필요시 회원가입, 기존유저와 이메일중복인경우 카카오 추가
	private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
		// DB 에 중복된 Kakao Id 가 있는지 확인
		Long kakaoId = kakaoUserInfo.getId();
		User kakaoUser = userRepository.findByKakaoId(kakaoId)
			.orElse(null);
		if (kakaoUser == null) {
			// 카카오 사용자 email과 동일한 email 가진 회원이 있는지 확인
			String kakaoEmail = kakaoUserInfo.getEmail();
			User sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
			if (sameEmailUser != null) {
				kakaoUser = sameEmailUser;
				// 중복 이메일이 있는경우 : 기존 회원정보에 카카오 Id 추가 (이메일이 같으면 동일 유저로 판단)
				kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
			} else {
				// 중복 kakaoId 값이 없는경우 : 카카오로 신규 회원가입
				// 카카오로그인으로 회원가입 한 경우 비밀번호는 랜덤 UUID 사용 (일반 로그인 폼 통한 로그인 방지)
				String password = UUID.randomUUID().toString();
				System.out.println(kakaoEmail);
				System.out.println(password);
				String encodedPassword = passwordEncoder.encode(password);

				// 이메일 : 카카오 이메일 사용
				String email = kakaoUserInfo.getEmail();

				kakaoUser = new User(kakaoUserInfo.getNickname(), kakaoId, encodedPassword, email);
			}

			userRepository.save(kakaoUser);
		}
		return kakaoUser;
	}

}
