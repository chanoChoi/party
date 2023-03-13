package com.example.party.user.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.party.application.entity.Application;
import com.example.party.chat.entity.EnrolledChatRoom;
import com.example.party.global.common.TimeStamped;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.dto.SignupRequest;
import com.example.party.user.type.Status;
import com.example.party.user.type.UserRole;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends TimeStamped implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	@Column(name = "email", nullable = false, unique = true, length = 50)
	private String email;
	@Column(name = "password", nullable = false, length = 60)
	private String password;
	@Column(name = "nickname", nullable = false, unique = true, length = 10)
	private String nickname;
	@Column
	private Long kakaoId; // 카카오 Oauth2 연동용(nullable=true) 카카오유저만 가짐

	// enum
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 12)
	private UserRole role;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVE', 'SUSPENDED', 'DORMANT')")
	private Status status;

	// 연관관계
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "profile_id", unique = true, referencedColumnName = "id")
	private Profile profile;
	@OneToMany(mappedBy = "user")
	private List<Application> applies;
	@OneToMany(mappedBy = "user")
	private List<PartyPost> partyPosts;
	@OneToMany(mappedBy = "user")
	private List<EnrolledChatRoom> enrolledChatRoom;
	@ManyToMany
	@JoinTable(name = "likes",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "post_id")
	)
	private Set<PartyPost> likePartyPosts;

	public String getProfileImg() {
		return this.profile.getProfileImg();
	}

	public String getComment() {
		return this.profile.getComment();
	}

	public int getNoShowCnt() {
		return this.profile.getNoShowCnt();
	}

	public int getParticipationCnt() {
		return this.profile.getParticipationCnt();
	}

	//일반생성자
	public User(SignupRequest signupRequest, String password) {
		this.email = signupRequest.getEmail();
		this.password = password;
		this.nickname = signupRequest.getNickname();
		this.role = UserRole.ROLE_USER;
		this.status = Status.ACTIVE;
		this.profile = new Profile();
	}

	//카카오생성자
	public User(String nickname, Long kakaoId, String password, String email) {
		this.kakaoId = kakaoId;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = UserRole.ROLE_USER;
		this.status = Status.ACTIVE;
		this.profile = new Profile();
	}

	//카카오 유저id 업데이트
	public User kakaoIdUpdate(Long kakaoId) {
		this.kakaoId = kakaoId;
		return this;
	}

	public void changeAdmin() {
		this.role = UserRole.ROLE_ADMIN;
	}

	public void DormantState() {
		this.status = Status.DORMANT;
	}

	public void setSuspended() {
		this.status = Status.SUSPENDED;
	}

	public void setActive() {
		this.status = Status.ACTIVE;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();
		for (UserRole eachRole : UserRole.values()) {
			authorities.add(new SimpleGrantedAuthority(eachRole.name()));
		}
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	public void updateProfile(ProfileRequest profileRequest) {
		this.nickname = profileRequest.getNickname();
	}

	public void increaseParticipationCnt() {
		this.profile.increaseParticipationCnt();
	}

	//작성한 참가신청 목록 추가
	public void addApplication(Application application) {
		this.applies.add(application);
	}

	//오브잭트 클레스에서 제공하는
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User)o;

		return id.equals(user.id);
	}
}
