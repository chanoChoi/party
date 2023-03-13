package com.example.party.global.util;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/*
프로그램 종료시 현재 redis 에 저장되어 있는 데이터를 삭제합니다.
* */
@RequiredArgsConstructor
@Component
public class ShutDownEventListener implements ApplicationListener<ContextClosedEvent> {
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		redisTemplate.execute((RedisConnection connection) -> {
			connection.flushAll();
			return "ok";
		});
	}

}
