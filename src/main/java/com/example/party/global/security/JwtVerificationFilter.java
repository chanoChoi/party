package com.example.party.global.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.global.util.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final RedisTemplate<String, String> redisTemplate;

	@Override // 로그인시 referer 사용 고려
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		JwtProvider.resolveToken(request)
			.ifPresent((token) -> {
				try {

					JwtProvider.validationToken(token);
					Long id = JwtProvider.getUserIdFromToken(token);
					if (valueOperations.get("rTKey" + id) == null) {
						JwtProvider.processExpire(token);
						throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
					}
					String email = JwtProvider.getEmailFromToken(token);
					setAuthentication(email);

				} catch (ExpiredJwtException eje) {
					Arrays.stream(request.getCookies())
						.filter(cookie -> Objects.equals(cookie.getName(), "rfToken"))
						.findFirst()
						.ifPresent(cookie -> {
							String refreshToken = cookie.getValue();
							Long id = JwtProvider.getUserIdFromToken(refreshToken);
							String savedRefreshToken = valueOperations.get("rTKey" + id);

							if (!refreshToken.equals(savedRefreshToken) || JwtProvider.validateExpire(
								savedRefreshToken)) { // 앞조건 더 생각해볼 것
								redisTemplate.delete("rTKey" + id);
								throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", eje);
							}

							String email = JwtProvider.getEmailFromToken(refreshToken);
							String newAccessToken = JwtProvider.accessToken(email, id);
							response.setHeader(JwtProvider.AUTHORIZATION_HEADER, "Bearer " + newAccessToken);
							System.out.println("==============");
							System.out.println("새로운 토큰 발행");
							System.out.println("기존 토큰 " + token);
							System.out.println("새로운 토큰 " + newAccessToken);
							System.out.println("==============");
							setAuthentication(email);
						});
				}
			});

		filterChain.doFilter(request, response);
	}

	private void setAuthentication(String email) {
		UserDetails user = userDetailsService.loadUserByUsername(email);
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
