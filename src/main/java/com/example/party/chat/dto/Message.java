package com.example.party.chat.dto;

import lombok.Getter;


@Getter
public class Message {
	public enum MessageType {
		SEND,
		OUT
	}

	private final MessageType messageType;
	private final String text;
	private final String roomId;
	private final String writer;

	public Message(String text, String messageType, String roomId, String writer) {
		this.text = text;
		this.messageType = MessageType.SEND.name().equals(messageType) ? MessageType.SEND : MessageType.OUT;
		this.roomId = roomId;
		this.writer = writer;
	}

}
