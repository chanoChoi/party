package com.example.party.category.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class CategoryNotActiveException extends BaseException {

	public static final String MSG = "비활성화된 카테고리입니다.";

	public CategoryNotActiveException() {
			super(HttpStatus.BAD_REQUEST, MSG);
	}

}
