package com.example.party.application.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class ApplicationNotGeneraleException extends BaseException {

	public ApplicationNotGeneraleException(String customMsg) {
		super(HttpStatus.BAD_REQUEST, customMsg);
	}
}
