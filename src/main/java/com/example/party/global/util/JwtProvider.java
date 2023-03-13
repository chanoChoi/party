package com.example.party.global.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer";
	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 임시로 작성해놓았습니다. 의견주시면 감사하겠습니다.
	private static final int expire = 1000;//30분
	private static final Long refreshExpire = 7 * 24 * 60 * 60 * 1000L; //1주일

	public static String accessToken(String email, Long userId) {
		Date curDate = new Date();
		Date expireDate = new Date(curDate.getTime() + expire);
		HashMap<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(header)
			.setSubject(email)
			.claim("id", userId)
			.setIssuedAt(curDate)
			.setExpiration(expireDate)
			.signWith(KEY)
			.compact();
	}

	public static String refreshToken(String email, Long userId) {
		Date curDate = new Date();
		Date refreshExpireDate = new Date(curDate.getTime() + refreshExpire);
		HashMap<String, Object> header = new HashMap<>();
		header.put("typ", "JWT");
		header.put("alg", "HS256");
		return Jwts.builder()
			.setHeader(header)
			.setSubject(email)
			.claim("id", userId)
			.setIssuedAt(curDate)
			.setExpiration(refreshExpireDate)
			.signWith(KEY)
			.compact();
	}

	public static Optional<String> resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return Optional.of(bearerToken.substring(6));
		}

		return Optional.empty();
	}

	public static Long getUserIdFromToken(String token) {
		return getClaimFromToken(token, claims -> claims.get("id", Long.class));
	}

	public static String getEmailFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public static long getExpiration(String token) {
		return getClaimFromToken(token, Claims::getExpiration).getTime() - new Date().getTime();
	}

	public static void validationToken(String token) throws ExpiredJwtException {
		try {
			Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
		} catch (SecurityException | MalformedJwtException e) {
			log.info("유효하지 않은 JWT 서명 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않은 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("잘못된 JWT 토큰 입니다.");
		}
	}

	private static Claims getAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private static <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
		final Claims claims = getAllClaims(token);
		return claimResolver.apply(claims);
	}

	public static boolean validateExpire(String refreshToken) {
		long time = getClaimFromToken(refreshToken, Claims::getExpiration).getTime() - new Date().getTime();
		return time <= 0;
	}

	public static void processExpire(String token) {
		getAllClaims(token).setExpiration(new Date());
	}
}
