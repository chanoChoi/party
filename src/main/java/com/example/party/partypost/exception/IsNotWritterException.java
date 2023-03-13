package com.example.party.partypost.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class IsNotWritterException extends BaseException {
	public static final String MSG = "해당 회원이 작성한 글이 아닙니다.";

	public IsNotWritterException() {
		super(HttpStatus.FORBIDDEN, MSG);
	}
}
