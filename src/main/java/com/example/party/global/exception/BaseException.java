package com.example.party.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
	private final HttpStatus status;

	public BaseException(HttpStatus status, String msg) {
		super(msg);
		this.status = status;
	}

	public BaseException(HttpStatus status) {
		this.status = status;
	}
}
