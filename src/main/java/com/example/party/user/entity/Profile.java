package com.example.party.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "img")
	private String img;
	@Column(name = "comment")
	private String comment;
	@Column(name = "no_show_cnt", nullable = false)
	private int noShowCnt;
	@Column(name = "participation_cnt", nullable = false)
	private int participationCnt;

	public Profile(String img, String comment, int noShowCnt, int participationCnt) {
		this.img = img;
		this.comment = comment;
		this.noShowCnt = noShowCnt;
		this.participationCnt = participationCnt;
	}

	public void updateProfile(String img, String comment) {
		this.img = img;
		this.comment = comment;
	}

	public void increaseParticipationCnt() {
		this.participationCnt = this.participationCnt + 1;
	}

}
