package com.example.party.application.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.party.application.dto.ApplicationResponse;
import com.example.party.application.entity.Application;
import com.example.party.application.exception.ApplicationNotFoundException;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.global.common.ApiResponse;
import com.example.party.global.common.DataApiResponse;
import com.example.party.global.exception.ForbiddenException;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.partypost.exception.PartyPostNotFoundException;
import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.user.entity.User;
import com.example.party.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ApplicationService implements IApplicationService {

	private final ApplicationRepository applicationRepository;
	private final PartyPostRepository partyPostRepository;
	private final UserRepository userRepository;
	private final ApplicationValidator applicationValidator;

	//모집글에 참가 신청
	@Override
	public ApiResponse createApplication(Long partyPostId, User user) {
		//0. 받아온 user 를 영속성 컨텍스트에 저장
		User user1 = userRepository.save(user);

		//1. partyPost 불러오기
		PartyPost partyPost = partyPostRepository.findById(partyPostId).orElseThrow(
			PartyPostNotFoundException::new
		);
		//2. Application 이 작성 가능한지 검증
		applicationValidator.validationApplicationBeforeCreation(partyPost, user1);
		//3. Application 객체 생성
		Application application = new Application(user1, partyPost);

		//4. repository 에 save
		applicationRepository.save(application);

		//5. 각 객체의 List 에 Application 저장
		partyPost.addApplication(application);
		user1.addApplication(application);

		//6.  DataResponseDto 생성 후 return
		return ApiResponse.ok("참가 신청 완료");
	}

	//참가신청 취소
	@Override
	public ApiResponse cancelApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isWrittenByMe(user.getId())) {
			throw new ForbiddenException();
		}
		application.cancel();

		return ApiResponse.ok("참가 신청 취소 완료");
	}

	//모집글의 참가신청 목록 조회(파티장만 조회가능)
	@Transactional(readOnly = true)
	@Override
	public DataApiResponse<ApplicationResponse> getApplications(Long partPostId, User user) {
		PartyPost partyPost = partyPostRepository.findById(partPostId)
			.orElseThrow(PartyPostNotFoundException::new);

		if (!partyPost.isWrittenByMe(user.getId())) {
			throw new ForbiddenException();
		}

		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
		Page<Application> applications = applicationRepository.findAllByPartyPostAndCancelIsFalse(
			partyPost,
			pageable);

		if (applications.getContent().size() == 0) {
			return DataApiResponse.ok("참가신청자 목록 조회 완료", Collections.emptyList());
		}

		List<ApplicationResponse> result = applications.map(ApplicationResponse::new).getContent();
		return DataApiResponse.ok("참가신청자 목록 조회 완료", result);
	}

	//(파티장) 참가신청 수락
	@Override
	public ApiResponse acceptApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isSendToMe(user.getId())) {
			throw new ForbiddenException();
		}

		applicationValidator.validateApplicationStatus(application);
		application.accept();
		return ApiResponse.ok("참가 신청 수락 완료");
	}

	//(파티장) 참가신청 거부
	@Override
	public ApiResponse rejectApplication(Long applicationId, User user) {
		Application application = getApplication(applicationId);

		if (!application.isSendToMe(user.getId())) {
			throw new ForbiddenException();
		}

		applicationValidator.validateApplicationStatus(application);
		application.reject();

		return ApiResponse.ok("참가 신청 거부 완료");
	}

	//단일 참가신청 객체 불러오기
	@Transactional(readOnly = true)
	public Application getApplication(Long applicationId) {
		return applicationRepository.findById(applicationId)
			.orElseThrow(ApplicationNotFoundException::new);
	}
}
