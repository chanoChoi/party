package com.example.party.partypost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatePartyPostRequest {

	//제목, 상세내용, 카테고리, 주소만 변경 가능 /현재 모임시작시간 & 모임마감시간 & 모집인원 변경 불가능
	private String title; //제목
	private String content; // 상세내용
	private long categoryId; // 음식
	private String partyAddress; // 서울 마포구 연남동 567-34
	private String partyPlace; // 파델라
}
