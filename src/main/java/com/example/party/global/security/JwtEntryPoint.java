package com.example.party.global.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtEntryPoint implements AuthenticationEntryPoint {
	private static final SecurityExceptionDto exceptionDto = new SecurityExceptionDto(HttpStatus.UNAUTHORIZED.value(),
		HttpStatus.UNAUTHORIZED.getReasonPhrase());

	@Override // XXX-Authentication
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		try (OutputStream os = response.getOutputStream()) {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(os, exceptionDto);
			os.flush();
		}
	}
}
