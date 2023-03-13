package com.example.party.user.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Entity
@Table(name = "Profile")
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "profile_img")
	private String profileImg;
	@Column(name = "comment")
	private String comment;
	@Column(name = "no_show_cnt", nullable = false)
	private int noShowCnt;

	@Column(name = "participation_cnt", nullable = false)
	private int participationCnt;
	private int adminReportCnt;

	public void setId(Long id) {
		this.id = id;
	}

	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	public Profile() {
		this.profileImg = "https://letsparty.s3.ap-northeast-2.amazonaws.com/static/unknown_user.png";
		this.comment = "상태메세지를 변경해주세요.";
		this.noShowCnt = 0;
		this.participationCnt = 0;
		this.adminReportCnt = 0;
	}

	public void updateProfile(String profileImg, String comment) {
		this.profileImg = profileImg;
		this.comment = comment;
	}

	public void plusAdminReportCnt() { this.adminReportCnt += 1; }
	public void plusNoShowCnt() {
		this.noShowCnt += 1;
	}
	public void minusNoShowCnt(int minusValue) { this.noShowCnt = noShowCnt - minusValue; }
	public void increaseParticipationCnt() {
		this.participationCnt = this.participationCnt + 1;
	}

}
