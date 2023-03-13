package com.example.party.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
	//카카오 로그인 서버에서 받아온 정보를 담기 위한 dto 입니다
	private Long id;
	private String email;
	private String nickname;

	//생성자
	public KakaoUserInfoDto(Long id, String nickname, String email) {
		this.id = id;
		this.nickname = nickname;
		this.email = email;
	}
}