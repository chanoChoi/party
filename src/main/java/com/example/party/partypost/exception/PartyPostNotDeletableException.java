package com.example.party.partypost.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class PartyPostNotDeletableException extends BaseException {

	public PartyPostNotDeletableException(String customMsg) {
		super(HttpStatus.NOT_FOUND, customMsg);
	}
}
