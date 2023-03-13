package com.example.party.chat.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.example.party.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String roomName;

	@OneToMany(mappedBy = "chatRoom")
	private List<EnrolledChatRoom> enrolledChatRooms;

	public ChatRoom(String roomName, List<User> users) {
		this.roomName = roomName;
		for (User user : users) {
			addUser(user);
		}
	}

	public void addUser(User user) {
		enrolledChatRooms.add(new EnrolledChatRoom(user, this));
	}

}
