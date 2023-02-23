package com.example.party.user.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Profiles")
public class Profiles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "img")
	private String img;
	@Column(name = "comment")
	private String comment;
	@Column(name = "no_show_cnt", nullable = false)
	private int noShowCnt;

	@Column(name = "participation_cnt", nullable = false)
	private int participationCnt;

	public void setId(Long id) {
		this.id = id;
	}

	public Profiles(String img, String comment, int participationCnt) {
		this.img = img;
		this.comment = comment;
		this.noShowCnt = 0;
		this.participationCnt = participationCnt;
	}

	public void updateProfile(String img, String comment) {
		this.img = img;
		this.comment = comment;
	}

	public void plusNoShowCnt() {
		this.noShowCnt += 1;
	}

	public void increaseParticipationCnt() {
		this.participationCnt = this.participationCnt + 1;
	}

}