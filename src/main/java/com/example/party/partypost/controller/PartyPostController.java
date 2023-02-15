package com.example.party.partypost.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ListResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.partypost.dto.PartyPostListResponse;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.PartyPostResponse;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.partypost.service.PartyPostService;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PartyPostController {

	private final PartyPostService partyPostService;
	private final PartyPostRepository partyPostRepository;

	//모집글 작성
	@PostMapping("/api/party-posts")
	public ResponseEntity<DataResponseDto<PartyPostResponse>> createPartyPost(
		@RequestBody PartyPostRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(partyPostService.createPartyPost(user, request));
	}

	//모집글 수정
	@PatchMapping("/api/party-posts/{partyPostId}")
	public ResponseEntity<DataResponseDto<PartyPostResponse>> updatePartyPost(
		@PathVariable Long partyPostId, @RequestBody PartyPostRequest request) {
		return ResponseEntity.ok(partyPostService.updatePartyPost(partyPostId, request));
	}

	@PostMapping("/likes/party-posts/{party_postId}")
	public DataResponseDto<String> toggleLikePartyPost(@PathVariable Long party_postId,
		@AuthenticationPrincipal User user) {
		//좋아요 기능
		return partyPostService.toggleLikePartyPost(party_postId, user.getId());
	}

	//모집글전체조회
	@GetMapping("")
	public ResponseEntity<ListResponseDto<PartyPostListResponse>> findPartyList() {
		return ResponseEntity.ok(partyPostService.findPartyList());
	}

	//모집글 상세 조회(개별 상세조회)
	@GetMapping("/{party-postId}")
	public ResponseEntity<DataResponseDto> getPartyPost(@PathVariable Long partyPostId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(partyPostService.getPartyPost(partyPostId, user));
	}

	//모집글 삭제
	@DeleteMapping("/{party-postId}")
	public ResponseEntity<ResponseDto> deletePartyPost(@PathVariable Long partyPostId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(partyPostService.deletePartyPost(partyPostId, user));
	}

}
