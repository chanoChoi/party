package com.example.party.partypost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyPostRequest {

	private String title; // 제목
	private String content; // 내용
	private Long categoryId; // 카테고리id
	private byte maxMember; // 총인원 (파티장 포함인원)
	private String partyDate; // 모임날짜
	private String partyAddress; // 서울 마포구 연남동 567-34
	private String partyPlace; // 파델라
}
