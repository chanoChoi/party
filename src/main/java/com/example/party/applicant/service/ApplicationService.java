package com.example.party.applicant.service;

import com.example.party.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.party.applicant.entity.Application;
import com.example.party.applicant.repository.ApplicationRepository;
import com.example.party.applicant.type.ApplicationResponse;
import com.example.party.global.dto.DataResponseDto;
import com.example.party.global.dto.ListResponseDto;
import com.example.party.global.dto.ResponseDto;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ApplicationService implements IApplicationService {

  private final ApplicationRepository applicationRepository;
  private final PartyPostRepository partyPostRepository;
  private final UserRepository userRepository;

  //모집 참가 신청
  @Override
  public DataResponseDto<String> createApplication(Long partyPostId, User user) {
    //1. partyPost 불러오기
    PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
        () -> new IllegalArgumentException("해당 postId의 partyPost 가 존재하지 않습니다")
    );
    //2. Application 객체 생성
    Application application = new Application(user, partyPost);
    //3. repository에 save
    applicationRepository.save(application);
    //4. Application 객체로 Response 에 보낼 Data 생성
    String title = partyPost.getTitle();
    //5.  DataResponseDto 생성 후 return
    return new DataResponseDto<>(HttpStatus.CREATED.value(), "참가 신청 완료", title);

  }

  @Override
  public ResponseDto cancelApplication(Long applicationId, User user) {
    Application application = getApplication(applicationId);
    if (!application.canCancel(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권환이 없습니다.");
    }
    application.cancel();

    return ResponseDto.ok("참가 신청 취소 완료");
  }

  @Transactional(readOnly = true)
  @Override
  public ListResponseDto<ApplicationResponse> getApplications(Long partPostId, User user) {
    PartyPost partyPost = partyPostRepository.findById(partPostId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT FOUND"));

    if (!partyPost.isWrittenByMe(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권환이 없습니다.");
    }

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<ApplicationResponse> ret = applicationRepository.findAllByPartyPostAndCancelIsFalse(
            partyPost.getId(),
            pageable)
        .map(ApplicationResponse::new);

    return ListResponseDto.ok("참가신청자 목록 조회 완료", ret.getContent());
  }

  @Override
  public DataResponseDto<ApplicationResponse> acceptApplication(Long applicationId, User user) {
    Application application = getApplication(applicationId);

    if (!application.canModify(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권환이 없습니다.");
    }
    application.accept();
    Application updatedApplication = applicationRepository.save(application);
    return DataResponseDto.ok("참가 신청 수락 완료", new ApplicationResponse(updatedApplication));
  }

  @Override
  public DataResponseDto<?> rejectApplication(Long applicationId, User user) {
    return null;
  }

  @Transactional(readOnly = true)
  public Application getApplication(Long applicationId) {
    return applicationRepository.findById(applicationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "application with id{" + applicationId + "} is not found"));
  }

}
