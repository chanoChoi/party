package com.example.party.user.service;

import javax.servlet.http.HttpServletResponse;

import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.user.dto.LoginRequest;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.dto.WithdrawRequest;
import com.example.party.user.entity.User;

public interface IUserService {
	//회원가입
	ResponseDto signUp(SignupRequest signupRequest);

	//로그인
	ResponseDto signIn(LoginRequest loginRequest, HttpServletResponse response);

	//로그아웃
	ResponseDto signOut(User user);

	//회원탈퇴
	ResponseDto withdraw(User user, WithdrawRequest withdrawRequest);

	//프로필 수정
	DataResponseDto<?> updateProfile(ProfileRequest profileRequest, Long id);

	//내 프로필 조회
	DataResponseDto<?> getMyProfile(User user);

	//상대방 프로필 조회
	DataResponseDto<?> getOtherProfile(Long id);
}
