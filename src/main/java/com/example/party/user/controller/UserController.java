package com.example.party.user.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.MyProfileResponse;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;
import com.example.party.user.service.UserService;
import com.example.party.util.JwtProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtProvider jwtProvider;

	@PostMapping("/signup")
	public ResponseEntity signup(@RequestBody @Valid SignupRequest signupRequest) {
		ResponseDto responseDto = userService.signUp(signupRequest);
		HttpHeaders headers = new HttpHeaders();
		return ResponseEntity.ok().headers(headers).body(responseDto);
	}

	@PostMapping("/signin")
	public ResponseEntity signin(@RequestBody LoginRequest loginRequest,
		HttpServletResponse response) {
		ResponseDto responseDto = userService.signIn(loginRequest, response);
		HttpHeaders headers = new HttpHeaders();
		return ResponseEntity.ok().headers(headers).body(responseDto);
	}

	@PostMapping("/signout")
	public ResponseEntity signout(@AuthenticationPrincipal User userDetails,
		HttpServletResponse response) {
		response.setHeader(jwtProvider.AUTHORIZATION_HEADER, "");
		userService.signOut(userDetails);
		return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity withdraw(@RequestBody WithdrawRequest withdrawRequest,
		@AuthenticationPrincipal User userDetails) {
		ResponseDto responseDto = userService.withdraw(userDetails, withdrawRequest);
		HttpHeaders headers = new HttpHeaders();
		return ResponseEntity.ok().headers(headers).body(responseDto);
	}

	//프로필 정보 수정
	@PatchMapping("/profile")
	public DataResponseDto<MyProfileResponse> updateProfile(
		@Validated @RequestBody ProfileRequest profileRequest,
		@AuthenticationPrincipal
		User user) {
		return userService.updateProfile(profileRequest, user.getId());
	}

	@GetMapping("/profile")
	public DataResponseDto<MyProfileResponse> getMyProfile(@AuthenticationPrincipal
	User user) {
		return userService.getMyProfile(user);
	}

	@GetMapping("/profile/{userId}")
	public DataResponseDto<MyProfileResponse> getOtherProfile(@PathVariable Long userId) {
		return userService.getOtherProfile(userId);
	}
}