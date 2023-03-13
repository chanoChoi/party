package com.example.party.user.controller;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.party.global.common.ApiResponse;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.service.KakaoService;
import com.example.party.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final KakaoService kakaoService;

	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
		return ResponseEntity.ok(userService.signUp(signupRequest));
	}

	//로그인
	@PostMapping("/signin")
	public ResponseEntity<ApiResponse> signIn(@RequestBody LoginRequest loginRequest) {
		String[] token = userService.signIn(loginRequest).split(",");
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token[0]);
		headers.add("Set-Cookie", String.format("rfToken=%s; Max-Age=604800; Path=/; HttpOnly=true;", token[1]));
		return ResponseEntity.ok().headers(headers).body(ApiResponse.ok("로그인 완료"));
	}

	//로그아웃
	@PostMapping("/signout")
	public ResponseEntity<ApiResponse> signOut(@AuthenticationPrincipal User user) {
		userService.signOut(user);
		HttpHeaders headers = new HttpHeaders();
		//accessToken 을 cookie에 넣기
		headers.add("Set-Cookie",
			String.format("Authorization=%s; Max-Age=0; Path=/page;", "Bearer " + ""));

		//RefreshToken 을 cookie에 넣기
		headers.add("Set-Cookie", String.format("rfToken=%s; Max-Age=0; Path=/; HttpOnly=true;", ""));

		headers.setLocation(URI.create("/page/indexPage"));
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}

	//회원탈퇴
	@PostMapping("/withdraw")
	public ResponseEntity<ApiResponse> withdraw(@RequestBody WithdrawRequest withdrawRequest,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.withdraw(user, withdrawRequest));
	}

	//프로필 정보 수정
	@PostMapping("/profile")
	public ResponseEntity<ApiResponse> updateProfile(
		@RequestPart(value = "file") MultipartFile file,
		@RequestPart(value = "dto") ProfileRequest profileRequest,
		@AuthenticationPrincipal User user) throws IOException {
		return ResponseEntity.ok(userService.updateProfile(user, profileRequest, file));
	}

	//내 프로필 조회
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMyProfile(@AuthenticationPrincipal
	User user) {
		return ResponseEntity.ok(userService.getMyProfile(user));
	}

	//상대 유저 프로필 조회
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ApiResponse> getOtherProfile(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getOtherProfile(userId));
	}

	//index페이지에서 로그인한 유저인지 확인
	@GetMapping("/loginCheck")
	@ResponseBody
	public Principal loginCheck(@AuthenticationPrincipal Principal principal) {
		return principal;
	}

	// 카카오 로그인 (카카오로부터 콜백 받음)
	@GetMapping("/kakao/callback")
	public ResponseEntity<Void> kakaoSignIn(@RequestParam String code, HttpServletResponse response) throws
		JsonProcessingException {
		String[] token = kakaoService.kakaoLogin(code, response).split(",");
		HttpHeaders headers = new HttpHeaders();

		System.out.println(token[0]);
		System.out.println(token[1]);

		headers.setBearerAuth(token[0]);

		//accessToken 을 cookie에 넣기
		headers.add("Set-Cookie",
			String.format("Authorization=%s; Max-Age=; Path=/page;", "Bearer " + token[0]));

		//RefreshToken 을 cookie에 넣기
		headers.add("Set-Cookie", String.format("rfToken=%s; Max-Age=604800; Path=/; HttpOnly=true;", token[1]));

		headers.setLocation(URI.create("/page/indexPage"));
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
