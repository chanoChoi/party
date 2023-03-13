package com.example.party.restriction.dto;

import com.example.party.restriction.type.ReportReason;

import lombok.Getter;

@Getter
public class ReportUserRequest {
	private Long userId;
	private ReportReason reason;
	private String detailReason;
}
