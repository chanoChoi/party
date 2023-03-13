package com.example.party.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.party.chat.dto.CreateRoomRequest;
import com.example.party.chat.dto.ChatRoomResponse;
import com.example.party.chat.service.ChatRoomService;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller

public class ChatRoomController {
	private final ChatRoomService chatRoomService;

	@GetMapping("/chatting")
	public String chatting(Model model, @AuthenticationPrincipal User user) {
		model.addAttribute("user", user);
		return "chat";
	}

	@PostMapping("/api/rooms")
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public void createRoom(@RequestBody CreateRoomRequest request, @AuthenticationPrincipal User user) {
		chatRoomService.createRoom(request, user);
	}

	@GetMapping("/api/rooms")
	@ResponseBody
	public List<ChatRoomResponse> getChatRoom(@AuthenticationPrincipal User user) { // 유저 정보
		return chatRoomService.findAllChatRoomByUser(user);
	}
}


