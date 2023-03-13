package com.example.party.category.exception;

import org.springframework.http.HttpStatus;

import com.example.party.global.exception.BaseException;

public class CategoryNotFoundException extends BaseException {

	public static final String MSG = "존재하지 않는 카테고리입니다.";

	public CategoryNotFoundException() {
		super(HttpStatus.NOT_FOUND, MSG);
	}
}
