package com.example.party.restriction.entity;

import javax.persistence.*;

import com.example.party.global.common.TimeStamped;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class NoShow extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(nullable = false)
	private Long reporterId;
	@Column(nullable = false)
	private Long reportedId;
	@Column(nullable = false)
	private Long partyPostId;

	public NoShow(Long reporterId, Long reportedId, Long partyPostId) {
		this.reporterId = reporterId;
		this.reportedId = reportedId;
		this.partyPostId = partyPostId;
	}
}
