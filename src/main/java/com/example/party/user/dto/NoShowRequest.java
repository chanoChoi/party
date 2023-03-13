package com.example.party.user.dto;

import lombok.Getter;

@Getter
public class NoShowRequest {
    private Long userId;
    private int minusValue;
}
