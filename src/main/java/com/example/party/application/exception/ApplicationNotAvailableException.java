package com.example.party.application.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class ApplicationNotAvailableException extends BaseException {
	private static final String msg = "변경가능한 신청서의 상태가 아닙니다.";

	public ApplicationNotAvailableException() {
		super(HttpStatus.NOT_FOUND, msg);
	}
}
