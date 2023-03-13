package com.example.party.partypost.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.type.Status;

import lombok.Getter;

@Getter
public class MyPartyPostListResponse {

	private final Long id; // partypost id
	private final Long ownerUserId;// 파티장 id
	private final String title; // 제목
	private final String status; // 모집글 상태
	private final byte maxMember; //총인원
	private final String partyDate; //모임날짜
	private final String closeDate; //마감날짜
	private final String address; //주소
	private final List<ApplicationResponse> joinMember; //참가한 맴버리스트

	public MyPartyPostListResponse(PartyPost partyPost, Long userId) {
		this.id = partyPost.getId();
		this.ownerUserId = partyPost.getUser().getId();
		this.title = partyPost.getTitle();
		this.status = changeStatusTextToKorean(partyPost.getStatus());
		this.maxMember = partyPost.getMaxMember();
		this.partyDate = partyPost.getPartyDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm"));
		this.closeDate = partyPost.getCloseDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm"));
		this.address = partyPost.getAddress();
		this.joinMember = partyPost.getApplications().stream()
			.filter(application -> !application.getUser().getId().equals(userId))
			.map(ApplicationResponse::new).collect(Collectors.toList());
	}

	public void removeJoinMember(ApplicationResponse applicationResponse) {
		this.joinMember.remove(applicationResponse);
	}

	public MyPartyPostListResponse(PartyPost partyPost) {
		this.id = partyPost.getId();
		this.ownerUserId = partyPost.getUser().getId();
		this.title = partyPost.getTitle();
		this.status = changeStatusTextToKorean(partyPost.getStatus());
		this.maxMember = partyPost.getMaxMember();
		this.partyDate = partyPost.getPartyDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) HH:mm").localizedBy(
			Locale.KOREAN));
		this.closeDate = partyPost.getCloseDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) HH:mm").localizedBy(
			Locale.KOREAN));
		this.address = partyPost.getAddress();
		this.joinMember = partyPost.getApplications().stream()
			.map(ApplicationResponse::new).collect(Collectors.toList());
	}

	private String changeStatusTextToKorean(Status partyPostStatus) {
		switch (partyPostStatus) {
			case FINDING:
				return "모집중";
			case FOUND:
				return "모집완료";
			case NO_SHOW_REPORTING:
				return "노쇼 투표 진행중";
			case PROCESSING:
				return "노쇼 결과 정산중";
			case END:
				return "종료됨";
			default:
				return partyDate.toString();
		}
	}
}

