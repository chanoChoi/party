package com.example.party;

import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.party.user.entity.User;

public class TestHelper {
	public static void withoutSecurity() {
		User user = mock(User.class);
		Authentication authentication = mock(Authentication.class);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(authentication.getPrincipal()).thenReturn(user);
	}
}
