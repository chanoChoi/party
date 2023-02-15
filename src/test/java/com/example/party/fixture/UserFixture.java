package com.example.party.fixture;

import com.example.party.user.entity.User;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;

public class UserFixture {
	public static User writer() {
		User user = new User("writer@mail.com", "password1", "nickname", "010-1234-1234", UserRole.ROLE_USER,
			Status.ACTIVE);
		user.setId(999L);
		return user;
	}

	public static User applicants() {
		User user = new User("applicants@mail.com", "password2", "nickname", "010-1234-1234", UserRole.ROLE_USER,
			Status.ACTIVE);
		user.setId(775L);
		return user;
	}
}
