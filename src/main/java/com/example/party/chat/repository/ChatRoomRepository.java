package com.example.party.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.party.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	@Query(value = "select ec.chatRoom from EnrolledChatRoom ec where ec.user.id=:userId")
	List<ChatRoom> findAllByUserId(@Param("userId") Long userId);

}
