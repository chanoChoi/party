package com.example.party.global.common;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DataApiResponse<T> extends ApiResponse {
	private final List<T> data;

	public DataApiResponse(int code, String msg, List<T> data) {
		super(code, msg);
		this.data = data;
	}

	public static <T> DataApiResponse<T> ok(String msg, List<T> data) {
		return new DataApiResponse<>(HttpStatus.OK.value(), msg, data);
	}

}
