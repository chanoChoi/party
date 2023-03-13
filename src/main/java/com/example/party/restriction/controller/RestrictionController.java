package com.example.party.restriction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.restriction.dto.BlockResponse;
import com.example.party.restriction.dto.NoShowRequest;
import com.example.party.restriction.dto.ReportPostRequest;
import com.example.party.restriction.dto.ReportUserRequest;
import com.example.party.restriction.service.RestrictionService;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restriction")
public class RestrictionController {

	private final RestrictionService restrictionService;

	//차단등록
	@PostMapping("/blocks/{userId}")
	public ResponseEntity<ApiResponse> blockUser(@PathVariable Long userId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.blockUser(user, userId));
	}

	//차단해제
	@DeleteMapping("/blocks/{userId}")
	public ResponseEntity<ApiResponse> unBlockUser(@PathVariable Long userId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.unBlockUser(user, userId));
	}

	//차단목록 조회
	@GetMapping("/blocks")
	public ResponseEntity<DataApiResponse<BlockResponse>> getBLockedList(@RequestParam int page,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.getBlockedList(page - 1, user));
	}

	//유저 신고
	@PostMapping("/report/users")
	public ResponseEntity<ApiResponse> createReportUser(@RequestBody ReportUserRequest request,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.createReportUser(user, request));
	}

	//모집글 신고
	@PostMapping("/report/party-posts")
	public ResponseEntity<ApiResponse> createReportPost(@RequestBody ReportPostRequest request,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.createReportPost(user, request));
	}

	//노쇼 신고
	@PostMapping("/noShow")
	public ResponseEntity<ApiResponse> noShowReport(@RequestBody NoShowRequest request,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(restrictionService.reportNoShow(user, request));
	}
}
