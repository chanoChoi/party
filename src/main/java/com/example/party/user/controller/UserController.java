package com.example.party.user.controller;

import static com.example.party.global.util.JwtProvider.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.party.global.common.ApiResponse;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
		ApiResponse apiResponse = userService.signUp(signupRequest);
		HttpHeaders headers = new HttpHeaders();
		return ResponseEntity.ok().headers(headers).body(apiResponse);
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
	public ResponseEntity<ApiResponse> signOut(@AuthenticationPrincipal User userDetails,
		HttpServletResponse response) {
		Cookie cookie = new Cookie("rfToken", null);
		cookie.setMaxAge(0);
		response.setHeader(AUTHORIZATION_HEADER, "");
		response.addCookie(cookie);
		System.out.println(userDetails);
		return ResponseEntity.ok(userService.signOut(userDetails));
	}

	//회원탈퇴
	@DeleteMapping("/withdraw")
	public ResponseEntity<ApiResponse> withdraw(@RequestBody WithdrawRequest withdrawRequest,
		@AuthenticationPrincipal User userDetails) {
		ApiResponse apiResponse = userService.withdraw(userDetails, withdrawRequest);
		HttpHeaders headers = new HttpHeaders();
		return ResponseEntity.ok().headers(headers).body(apiResponse);
	}

	//프로필 정보 수정
	@PatchMapping("/profile")
	public ResponseEntity<ApiResponse> updateProfile(
		@Validated @RequestBody ProfileRequest profileRequest,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.updateProfile(profileRequest, user));
	}

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse> getMyProfile(@AuthenticationPrincipal
	User user) {
		return ResponseEntity.ok(userService.getMyProfile(user));
	}

	@GetMapping("/profile/{userId}")
	public ResponseEntity<ApiResponse> getOtherProfile(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getOtherProfile(userId));
	}
}