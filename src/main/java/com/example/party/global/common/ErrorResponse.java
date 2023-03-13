package com.example.party.global.common;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {
	private final int code;
	private final String error;
	private final String msg;
	private final String path;
	private final LocalDateTime timestamp;

	public ErrorResponse(int code, String error, String msg, String path) {
		this.code = code;
		this.error = error;
		this.msg = msg;
		this.path = path;
		this.timestamp = LocalDateTime.now();
	}
}
