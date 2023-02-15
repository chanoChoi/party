package com.example.party.partypost.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PartyPostRequest {

  private String title;
  private String content;
  private byte maxMember;
  private LocalDateTime partyDate;
  private String eubMyeonDong;
  private String address;
  private String detailAddress;
}