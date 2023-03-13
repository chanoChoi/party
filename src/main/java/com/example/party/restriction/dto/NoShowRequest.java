package com.example.party.restriction.dto;

import lombok.Getter;

@Getter
public class NoShowRequest {
	private Long userId;
	private Long partyPostId;
}
