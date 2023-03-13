package com.example.party.restriction.dto;

import com.example.party.restriction.type.ReportReason;

import lombok.Getter;

@Getter
public class ReportPostRequest {
	private Long postId;
	private ReportReason reason;
	private String detailReason;
}
