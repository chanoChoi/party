package com.example.party.partypost.dto;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.type.Status;

import lombok.Getter;

// 검색결과 , 핫한 모집글 조회결과
@Getter
public class PartyPostListResponse {

	//postId
	private final Long postId;
	//모집글의 제목
	private final String title;
	//파티장 닉네임
	private final String partyOwner;
	//모집글의 상태
	private final String status;
	//현재 인원
	private final byte acceptedMember;
	//총인원
	private final byte maxMember;
	//partyDate(모임시작시간)
	private final String partyDate;
	//closeDate(모집마감시간)
	private final String closeDate;
	//모임 주소(서울 마포구 연남동) 까지만
	private final String partyAddress;
	//모임 장소 (파델라)
	private final String partyPlace;
	//파티장 프로필 이미지
	private final String profileImg;

	public PartyPostListResponse(PartyPost partyPost) {
		this.postId = partyPost.getId();
		this.title = partyPost.getTitle();
		this.partyOwner = partyPost.getUser().getNickname();
		this.status = changeStatusTextToKorean(partyPost.getStatus());
		this.acceptedMember = partyPost.getAcceptedMember();
		this.maxMember = partyPost.getMaxMember();
		this.partyDate = partyPost.getPartyDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm").localizedBy(
			Locale.KOREAN));
		this.closeDate = partyPost.getCloseDate().format(DateTimeFormatter.ofPattern("yy/MM/dd(E) a hh:mm").localizedBy(
			Locale.KOREAN));
		this.partyAddress = partyPost.getAddress();
		this.partyPlace = partyPost.getPartyPlace();
		this.profileImg = partyPost.getUser().getProfile().getProfileImg();
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
