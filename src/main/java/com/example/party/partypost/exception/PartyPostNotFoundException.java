package com.example.party.partypost.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class PartyPostNotFoundException extends BaseException {

	public static final String MSG = "존재하지 않는 모집글 입니다.";

	public PartyPostNotFoundException() {
		super(HttpStatus.NOT_FOUND, MSG);
	}
}
