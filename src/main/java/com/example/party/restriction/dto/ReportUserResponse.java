package com.example.party.restriction.dto;

import com.example.party.restriction.entity.ReportUser;
import com.example.party.restriction.type.ReportReason;

import lombok.Getter;

@Getter
public class ReportUserResponse {
	private final Long id; //신고 이벤트의 Id
	private final Long reportedId; //신고당한 유저 Id
	private final String userEmail;
	private final ReportReason reason;
	private final String detailReason;

	public ReportUserResponse(ReportUser reportUser) {
		this.id = reportUser.getId();
		this.reportedId = reportUser.getReported().getId();
		this.userEmail = reportUser.getReported().getEmail();
		this.reason = reportUser.getReason();
		this.detailReason = reportUser.getDetailReason();
	}
}
