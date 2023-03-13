package com.example.party.user.dto;

import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@{1}(.{1}){2,50}$",
		message = "대소문자, 숫자, 1개의 .과 @ 사용한 최소 2자 최대 50자")
	private String email;
	@Pattern(regexp = "^[A-Za-z0-9~!@#$%^&*=,.?]{8,60}$",
		message = "대소문자, 숫자, 특수문자 범주 안에 최소 8자 최대 60자")
	private String password;
	@Pattern(regexp = "^[A-Za-z0-9가-힣+_]{2,10}$",
		message = "대소문자, 숫자, 한글 범주 안에 최소 2자 최대 10자")
	private String nickname;
}
