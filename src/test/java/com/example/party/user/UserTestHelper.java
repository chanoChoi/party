package com.example.party.user;

import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;

public class UserTestHelper {
	public static SignupRequest signupRequest() {
		return new SignupRequest(
			"test@email.com",
			"password1!",
			"nickname"
		);
	}

	public static LoginRequest loginRequest() {
		return new LoginRequest(
			"test@eamil.com",
			"password1!"
		);
	}

	public static WithdrawRequest withdrawRequest() {
		return new WithdrawRequest("password1!");
	}

	public static ProfileRequest profilesRequest() {
		return new ProfileRequest(
			"nickname",
			"profileImg",
			"comment"
		);
	}
}
