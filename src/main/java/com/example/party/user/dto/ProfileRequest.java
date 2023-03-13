package com.example.party.user.dto;

import com.example.party.global.exception.BadRequestException;
import com.example.party.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileRequest {
	private String nickname; //user
	private String profileImg; //profile
	private String comment; //profile
	private final String empty = "";

	public ProfileRequest(String nickname, String profileImg, String comment) {
		this.nickname = nickname;
		this.profileImg = profileImg;
		this.comment = comment;
		if (profileImg.equals(empty) && comment.equals(empty) && nickname.equals(empty)) {
			throw new BadRequestException("값을 입력해 주세요");
		}
	}

	public ProfileRequest checkingInput(User user) {
		if (profileImg == null) {
			this.profileImg = user.getProfileImg();
		}
		if (comment.equals(empty)) {
			this.comment = user.getComment();
		}
		if (nickname.equals(empty)) {
			this.nickname = user.getNickname();
		}
		return this;
	}
}
