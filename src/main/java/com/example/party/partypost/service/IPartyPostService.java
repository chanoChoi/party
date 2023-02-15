package com.example.party.partypost.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ListResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.partypost.dto.PartyPostListResponse;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.PartyPostResponse;
import com.example.party.user.entity.User;

public interface IPartyPostService {

	//모집글 작성
	DataResponseDto<PartyPostResponse> createPartyPost(User user, PartyPostRequest request);

	//모집글 수정
	DataResponseDto<PartyPostResponse> updatePartyPost(Long partyPostId, PartyPostRequest request);

	//내가 작성한 모집글 리스트 조회 ( 내가 파티장인 경우만 )
	ListResponseDto<PartyPostListResponse> findMyCreatedPartyList();

	//내가 참석한 모집글 리스트 조회( 내가 파티원인 경우만 )
	ListResponseDto<PartyPostListResponse> findMyJoinedPartyList();

	//모집게시물 좋아요 (*좋아요 취소도 포함되는 기능임)
	DataResponseDto<String> toggleLikePartyPost(Long party_postId, Long userId);

	//모집글전체조회
	ListResponseDto<?> findPartyList();

	//모집글 상세 조회(개별 상세조회)
	@Transactional
	DataResponseDto<PartyPostResponse> getPartyPost(Long postId, User user);

	//모집글 삭제
	ResponseDto deletePartyPost(Long partyPostId, User user);
}
