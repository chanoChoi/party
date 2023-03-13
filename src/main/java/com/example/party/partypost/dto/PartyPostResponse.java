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
public class PartyPostResponse {

	private final Long id;
	private final Long userId; // 파티장 Id
	private final String nickname;
	private final String profileImg; //파티장 프로필 이미지
	private final String title;
	private final String content;
	private final Long categoryId;
	private final String categoryName;
	private final String status;
	private final byte acceptedMember;
	private final byte maxMember;
	private final String partyDate;
	private final String closeDate;
	private final String address;
	private final String detailAddress;
	private final String partyPlace;
	private final int viewCnt;
	private final List<ApplicationResponse> joinMember;

	//생성자
	public PartyPostResponse(PartyPost partyPost) {
		this.id = partyPost.getId();
		this.userId = partyPost.getUser().getId();
		this.nickname = partyPost.getUser().getNickname();
		this.profileImg = partyPost.getUser().getProfile().getProfileImg();
		this.title = partyPost.getTitle();
		this.content = partyPost.getContent();
		this.categoryId = partyPost.getCategory().getId();
		this.categoryName = partyPost.getCategory().getName();
		this.viewCnt = partyPost.getViewCnt();
		this.partyPlace = partyPost.getPartyPlace();
		this.acceptedMember = partyPost.getAcceptedMember();
		this.status = changeStatusTextToKorean(partyPost.getStatus());
		this.maxMember = partyPost.getMaxMember();
		this.partyDate = partyPost.getPartyDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm").localizedBy(
			Locale.KOREAN));
		this.closeDate = partyPost.getCloseDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm").localizedBy(
			Locale.KOREAN));
		this.address = partyPost.getAddress();
		this.detailAddress = partyPost.getDetailAddress();
		this.joinMember = partyPost.getApplications().stream().map(
			ApplicationResponse::new).collect(Collectors.toList());
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
