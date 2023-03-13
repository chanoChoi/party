package com.example.party.global.config;

import com.example.party.global.security.JwtAccessDeniedHandler;
import com.example.party.global.security.JwtEntryPoint;
import com.example.party.global.security.JwtVerificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfiguration {
	public static final String[] URL_PERMIT_ALL = "/api/users/kakao/callback,/api/users/signup,/api/users/signin,/api/party-posts,/api/party-posts/{party-postId:[\\d+]},/api/party-posts/hot,/api/party-posts/near/{Address},/api/party-posts/search/**,/api/categories,/api/party-posts/categories/{categoryId}".split(
		",");
	public static final String[] URL_ROLE_USER_ADMIN = "/api/restriction/**,/api/users/**,/api/party-posts/**,/api/rooms,chatting".split(
		",");
	private final UserDetailsService userDetailsService;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public JwtEntryPoint entryPoint() {
		return new JwtEntryPoint();
	}

	@Bean
	public JwtAccessDeniedHandler accessDeniedHandler() {
		return new JwtAccessDeniedHandler();
	}

	@Bean
	public JwtVerificationFilter jwtVerificationFilter() {
		return new JwtVerificationFilter(userDetailsService, redisTemplate);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			// .requestMatchers(PathRequest.toH2Console())
			.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.addFilterBefore(jwtVerificationFilter(), UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(config -> config
				.authenticationEntryPoint(entryPoint())
				.accessDeniedHandler(accessDeniedHandler())
			);

		http
			.authorizeHttpRequests(auth -> auth
				.antMatchers(URL_PERMIT_ALL)
				.permitAll()
				.antMatchers(URL_ROLE_USER_ADMIN)
				.hasAnyRole("USER", "ADMIN")
				.antMatchers("/api/categories/**")
				.hasRole("ADMIN")
				.antMatchers("/api/applications/**")
				.hasRole("USER")
				.antMatchers("/api/admin/**")
				.hasRole("ADMIN")
				.anyRequest()
				.permitAll()
			)
			.formLogin(login -> login
				.loginPage("/page/loginPage")
				.successForwardUrl("/page/indexPage")
			);

		return http.build();
	}
}
