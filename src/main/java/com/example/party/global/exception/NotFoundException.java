package com.example.party.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

	private static String msg = "요청한 정보 또는 자원이 존재하지 않습니다";
	public NotFoundException() {
		super(HttpStatus.NOT_FOUND, msg);
	}
}
