package com.example.party.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ForbiddenException extends BaseException {
	private static final String msg = "허가되지 않은 요청입니다.";

	public ForbiddenException() {
		super(HttpStatus.FORBIDDEN, msg);
	}
}
