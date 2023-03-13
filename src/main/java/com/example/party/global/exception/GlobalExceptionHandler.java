package com.example.party.global.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.party.global.common.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, BaseException e) {
		return ResponseEntity.status(e.getStatus())
			.body(new ErrorResponse(
				e.getStatus().value(),
				e.getStatus().name(),
				e.getMessage(),
				request.getRequestURI()
			));
	}
}
