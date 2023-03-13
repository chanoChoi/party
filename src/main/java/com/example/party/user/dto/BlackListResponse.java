package com.example.party.user.dto;

import com.example.party.user.entity.User;
import lombok.Getter;

@Getter
public class BlackListResponse {
    private Long userId;
    private String email;
    private String nickname;
    private final String msg;

    public BlackListResponse(User user, String msg) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.msg = msg;
    }
}
