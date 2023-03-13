package com.example.party.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.application.service.ApplicationService;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/applications")
@RestController
public class ApplicationController {

	private final ApplicationService applicationService;

	//모집글작성
	@PostMapping("/join/{party-postId}")
	public ResponseEntity<ApiResponse> createApplication(@PathVariable(name = "party-postId") Long partyPostId,
		@AuthenticationPrincipal User user) {

		return ResponseEntity.ok(applicationService.createApplication(partyPostId, user));
	}

	//모집참가취소
	@PostMapping("/cancel/{applicationId}")
	public ResponseEntity<ApiResponse> cancelApplication(@PathVariable Long applicationId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(applicationService.cancelApplication(applicationId, user));
	}

	//해당 모집글의 참가신청 조회
	@GetMapping("/{party-postId}")
	public ResponseEntity<DataApiResponse<ApplicationResponse>> getApplications(
		@PathVariable(name = "party-postId") Long partyPostId,
		@AuthenticationPrincipal User user
	) {
		// User user = new User(new SignupRequest("email@email.com", "password1!", "nickname", "010-1234-1234"), "asdadasdasdasdasdasd");
		return ResponseEntity.ok(applicationService.getApplications(partyPostId, user));
	}

	//(파티장) 참가신청 수락
	@PostMapping("/accept/{applicationId}")
	public ResponseEntity<ApiResponse> acceptApplication(
		@PathVariable Long applicationId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(applicationService.acceptApplication(applicationId, user));
	}

	//(파티장) 참가신청 거부
	@PostMapping("/reject/{applicationId}")
	public ResponseEntity<ApiResponse> rejectApplication(@PathVariable Long applicationId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(applicationService.rejectApplication(applicationId, user));
	}

}
