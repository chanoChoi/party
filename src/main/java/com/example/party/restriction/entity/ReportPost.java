package com.example.party.restriction.entity;

import javax.persistence.*;

import com.example.party.global.common.TimeStamped;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.restriction.dto.ReportPostRequest;
import com.example.party.restriction.type.ReportReason;
import com.example.party.user.entity.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ReportPost extends TimeStamped {

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
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partyPost_id")
	private PartyPost partyPost;

	public ReportPost(User user, ReportPostRequest request, PartyPost partyPost) {
		this.user = user;
		this.reason = request.getReason();
		this.detailReason = request.getDetailReason();
		this.partyPost = partyPost;
	}
}
