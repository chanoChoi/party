package com.example.party.user.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class EmailOverlapException extends BaseException {

	public static final String MSG = "해당 이메일이 이미 존재합니다";

	public EmailOverlapException() {
		super(HttpStatus.BAD_REQUEST, MSG);

	}
}