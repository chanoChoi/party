package com.example.party.partypost.dto;

import com.example.party.applicant.entity.Application;

import lombok.Getter;

@Getter
public class ApplicationResponse {

	private final Long id;
	private final String nickname;
	private final String profileImg;

	public ApplicationResponse(Application application) {
		this.id = application.getId();
		this.nickname = application.getUser().getNickname();
		this.profileImg = application.getUser().getProfile().getImg();
	}
}
