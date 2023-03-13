package com.example.party.partypost.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.party.application.entity.Application;
import com.example.party.application.type.ApplicationStatus;
import com.example.party.category.entity.Category;
import com.example.party.global.common.TimeStamped;
import com.example.party.partypost.dto.PartyPostRequest;
import com.example.party.partypost.dto.UpdatePartyPostRequest;
import com.example.party.partypost.type.Status;
import com.example.party.restriction.entity.ReportPost;
import com.example.party.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "party_post")
@Entity(name = "partyPost")
public class PartyPost extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "title", nullable = false, length = 50)
	private String title; // 제목
	@Column(name = "content", nullable = false, columnDefinition = "TEXT")
	private String content; // 내용
	@Column(name = "view_cnt", nullable = false)
	private int viewCnt; //조회수
	@Column(name = "max_member", nullable = false)
	private byte maxMember; //총인원 (파티장 포함 인원)
	@Column(name = "address", nullable = false)
	private String address; // 주소 (ex. 서울 마포구 연남동)
	@Column(name = "detail_address", nullable = false)
	private String detailAddress; // 동이하 상세주소 (ex. 567-34)
	@Column(name = "partyPlace", nullable = false)
	private String partyPlace; // 모임장소 (ex.파델라)

	@Column(name = "is_active", nullable = false)
	private boolean active; // 삭제 여부 (false = 삭제)
	@Column(name = "accepted_cnt", nullable = false)
	private byte acceptedMember; //수락된 인원(ACCEPT 시 올라감. 파티장 미포함)

	// enum
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "ENUM('FINDING', 'FOUND', 'NO_SHOW_REPORTING', 'PROCESSING', 'END')")
	private Status status; // 모집상태 FINDING 모집중 / FOUND 모집완료 / NO_SHOW_REPORTING 노쇼신고가능 / END 모임종료 ( 상세설명은 type.Status 에서 확인가능)
	@Column(name = "close_date", nullable = false)
	private LocalDateTime closeDate; //마감시간 (partyDate 에서 15분전)
	@Column(name = "party_date", nullable = false)
	private LocalDateTime partyDate; //모임시간

	// 연관관계
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user; //작성자
	@OneToMany(mappedBy = "partyPost")
	private List<Application> applications; //이 모집글에 작성된 참가신청
	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category; //카테고리
	@OneToMany(mappedBy = "partyPost")
	private List<ReportPost> reportPosts;

	//생성자
	public PartyPost(User user, PartyPostRequest request, LocalDateTime partyDate, Category category) {
		this.user = user;
		this.title = request.getTitle();
		this.content = request.getContent();
		this.category = category;
		this.status = Status.FINDING;
		this.maxMember = request.getMaxMember();
		this.address = extractAddress(request.getPartyAddress());
		this.detailAddress = extractDetailAddress(request.getPartyAddress());
		this.partyPlace = request.getPartyPlace();
		this.partyDate = partyDate;
		this.closeDate = partyDate.minusMinutes(15);
		this.applications = new ArrayList<>();
		this.active = true;
	}

	//제목, 상세내용, 카테고리, 주소만 변경 가능 /현재 모임시작시간 & 모임마감시간 & 모집인원 변경 불가능
	public void update(UpdatePartyPostRequest request, Category category) {
		this.title = request.getTitle();
		this.content = request.getContent();
		this.category = category;
		this.partyPlace = request.getPartyPlace();
		this.address = extractAddress(request.getPartyAddress());
		this.detailAddress = extractDetailAddress(request.getPartyAddress());
	}

	// public 메소드
	// 조회수 증가
	public void increaseViewCnt(User user) {
		if (!this.user.equals(user)) {
			this.viewCnt += 1;
		}
	}

	public void clearApplications() {
		this.applications.clear();
	}
	//모집마감전인지 확인
	public boolean beforeCloseDate(LocalDateTime now) {
		return this.closeDate.isAfter(now);
	}

	//참가신청한 모집자가 없는지 확인
	public boolean haveNoApplications() {
		return this.applications.isEmpty();
	}

	//모집글 삭제(DB에서 삭제하지 않고, active 값으로 관리합니다)
	public void deletePartyPost() {
		this.active = false;
	}

	//작성자확인
	public boolean isWrittenByMe(Long userId) {
		return Objects.equals(this.user.getId(), userId);
	}

	// 들어온 참가신청을 applications 에 추가
	public void addApplication(Application application) {
		this.applications.add(application);
	}

	// 이미 참가신청한 유저인지 확인
	public boolean isAlreadyApplied() {
		return false;
	}

	//모집상태(FINDING)인지 확인
	public boolean isFinding() {
		return this.status == Status.FINDING;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void increaseAcceptedCnt() {
		byte curMember = (byte)(this.acceptedMember+1);
		if (curMember  == this.maxMember) {
			this.status = Status.FOUND;
			isFullParty();
		}
		this.acceptedMember = curMember;
	}

	public void ChangeStatusEnd() {
		this.status = Status.END;
	}

	//private 메소드

	//request 에서 받아온 주소에서 address(~동 까지) 추출
	private String extractAddress(String partyAddress) {
		int index = partyAddress.indexOf("동 ");

		return partyAddress.substring(0, index + 1);
	}

	//request 에서 받아온 주소에서 detailAddress(상세주소) 추출
	private String extractDetailAddress(String partyAddress) {
		int index = partyAddress.indexOf("동 ");

		return partyAddress.substring(index + 2, partyAddress.length());
	}

	//모집글이 FOUND가 된 경우 아직 PENDING 상태인 모집글을 모두 REJECT로 전환
	private void isFullParty() {
		this.applications.stream()
			.filter(application -> application.getStatus().equals(ApplicationStatus.PENDING))
			.forEach(
				Application::reject);
	}

	// Test 하기 위한 코드
	public void changeStatusNoShow() {
		this.status = Status.NO_SHOW_REPORTING;
	}
}
// TODO: API 1차 작업완료 후
// 차단한 유저의 게시물 블라인드 처리 방식 생각


