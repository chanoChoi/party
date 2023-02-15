package com.example.party.partypost.dto;

import com.example.party.applicant.type.ApplicationResponse;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.type.Status;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class PartyPostResponse {

  private final Long id;
  private final String title;
  private final String content;
  private final Status status;
  private final LocalDateTime createdAt;
  private final LocalDateTime modifiedAt;
  private final byte maxMember;
  private final LocalDateTime partyDate;
  private final LocalDateTime closeDate;
  private final String day;
  private final String eubMyeonDong;
  private final String address;
  private final String detailAddress;
  private final int viewCnt;
  private final List<ApplicationResponse> joinMember;

  //partyDate 를 넣어주면 요일을 계산해주는 메소드 (ex) 토
  private String whichDayOfWeek(LocalDateTime partyDate) {
    return partyDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
  }

  //PartyPost Entity의 List<Application> 을 applicationDto 로 바꿔주는 메소드
  private List<ApplicationResponse> makeApplications(PartyPost partyPost) {

    return partyPost.getApplications().stream().map(
        ApplicationResponse::new).collect(
        Collectors.toList());
  }

  //생성자
  public PartyPostResponse(PartyPost partyPost) {
    this.id = partyPost.getId();
    this.title = partyPost.getTitle();
    this.content = partyPost.getContent();
    this.viewCnt = partyPost.getViewCnt();
    this.status = partyPost.getStatus();
    this.createdAt = partyPost.getCreatedAt();
    this.modifiedAt = partyPost.getModifiedAt();
    this.maxMember = partyPost.getMaxMember();
    this.partyDate = partyPost.getPartyDate();
    this.closeDate = partyPost.getCloseDate();
    this.day = partyDate.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);

    this.eubMyeonDong = partyPost.getEubMyeonDong();
    this.address = partyPost.getAddress();
    this.detailAddress = partyPost.getDetailAddress();
    this.joinMember = partyPost.getApplications().stream().map(
        ApplicationResponse::new).collect(Collectors.toList());
  }
}