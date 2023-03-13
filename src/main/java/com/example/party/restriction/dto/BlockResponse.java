package com.example.party.restriction.dto;

import com.example.party.restriction.entity.Block;

import lombok.Getter;

@Getter
public class BlockResponse {
	private final Long blockedId; //내가 차단한 유저 Id
	private final String nickname; //내가 차단한 유저 닉네임

	public BlockResponse(Block block) {
		this.blockedId = block.getBlocked().getId();
		this.nickname = block.getBlocked().getNickname();
	}
}
