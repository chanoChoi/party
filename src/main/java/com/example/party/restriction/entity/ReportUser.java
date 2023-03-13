package com.example.party.restriction.entity;

import javax.persistence.*;

import com.example.party.global.common.TimeStamped;
import com.example.party.restriction.dto.ReportUserRequest;
import com.example.party.restriction.type.ReportReason;
import com.example.party.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ReportUser extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(name = "reason", nullable = false, columnDefinition = "ENUM('SPAM', 'HARASSMENT', 'INAPPROPRIATE_CONTENT', 'ETC')")
	private ReportReason reason;
	@Column(nullable = false)
	private String detailReason;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id")
	private User reporter;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_id")
	private User reported;

	public ReportUser(User reporter, User reported, ReportUserRequest request) {
		this.reporter = reporter;
		this.reported = reported;
		this.reason = request.getReason();
		this.detailReason = request.getDetailReason();
	}
}
