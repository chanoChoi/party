package com.example.party.restriction.entity;

import javax.persistence.*;

import com.example.party.global.common.TimeStamped;
import com.example.party.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Block extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private User blocker;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private User blocked;

	public Block(User blocker, User blocked) {
		this.blocker = blocker;
		this.blocked = blocked;
	}
}

