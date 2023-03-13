package com.example.party.application.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.party.application.type.ApplicationStatus;
import com.example.party.global.common.TimeStamped;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table
@Entity
public class Application extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "is_cancel", nullable = false)
	private boolean cancel;

	// enum
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDING', 'ACCEPT', 'REJECT')")
	private ApplicationStatus status;

	// 연관관계
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(optional = false)
	@JoinColumn(name = "party_post_id")
	private PartyPost partyPost;

	public Application(User user, PartyPost partyPost) {
		this.cancel = false;
		this.status = ApplicationStatus.PENDING;
		this.user = user;
		this.partyPost = partyPost;
	}

	public boolean isWrittenByMe(Long userId) {
		return Objects.equals(this.user.getId(), userId);
	}

	public void cancel() {
		this.cancel = true;
	}

	public String getNickname() {
		return this.user.getNickname();
	}

	public String getProfileImg() {
		return this.user.getProfileImg();
	}

	public int getNoShowCnt() {
		return this.user.getNoShowCnt();
	}

	public boolean isSendToMe(Long userId) {
		return Objects.equals(this.partyPost.getUser().getId(), userId);
	}

	public void accept() {
		this.status = ApplicationStatus.ACCEPT;
		this.user.increaseParticipationCnt();
		this.partyPost.increaseAcceptedCnt();
	}

	public void reject() {
		this.status = ApplicationStatus.REJECT;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
