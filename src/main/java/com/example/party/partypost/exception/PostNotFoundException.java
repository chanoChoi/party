package com.example.party.partypost.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class PostNotFoundException extends BaseException {

	private static final String msg = "해당 글이 존재 하지 않습니다.";

	public PostNotFoundException() {
		super(HttpStatus.NOT_FOUND, msg);
	}

	public PostNotFoundException(String customMsg) {
		super(HttpStatus.NOT_FOUND, customMsg);
	}
}
