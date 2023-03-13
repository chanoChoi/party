package com.example.party.application.service;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.user.entity.User;

public interface IApplicationService {

	//모집 참가 신청
	ApiResponse createApplication(Long partyPostId, User user);

	//모집 참가 신청취소
	ApiResponse cancelApplication(Long applicationId, User user);

	//참가신청자 리스트 조회(파티장 미포함. 내정보-나의모집글목록 상에서 사용)
	DataApiResponse<ApplicationResponse> getApplications(Long partyPostId, User user);

	//모집 참가 수락
	ApiResponse acceptApplication(Long applicationId, User user);

	//모집 참가 거부
	ApiResponse rejectApplication(Long applicationId, User user);

}
