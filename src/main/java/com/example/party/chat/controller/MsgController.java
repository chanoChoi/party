package com.example.party.chat.controller;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.party.chat.dto.Message;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MsgController {
	private final SimpMessagingTemplate template; // 높은 추상화. 사용하기 더 편함

	@MessageExceptionHandler
	@SendTo("/errors")
	public String error(Exception e)  {
		return "Something Wrong" + NestedExceptionUtils.getMostSpecificCause(e);
	}

	@MessageMapping("/rooms/message")
	public void sendMessage(@RequestBody Message message) {
		System.out.println("/queue/rooms/" + message.getRoomId());
		template.convertAndSend("/queue/rooms/" + message.getRoomId(), message);
	}
}
