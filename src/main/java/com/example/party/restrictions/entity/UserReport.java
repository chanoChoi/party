package com.example.party.restrictions.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.party.restrictions.dto.ReportUserRequest;
import com.example.party.restrictions.type.ReportReason;
import com.example.party.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportReason reason;

	@Column(nullable = false)
	private String details;

	@ManyToOne
	@JoinColumn(nullable = false)
	private User reporter;
	@ManyToOne
	@JoinColumn(nullable = false)
	private User reported;

	public UserReport(User reporter, User reported, ReportUserRequest request) {
		this.reporter = reporter;
		this.reported = reported;
		this.reason = request.getReason();
		this.details = request.getDetails();
	}
}