package com.example.party.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.party.application.entity.Application;
import com.example.party.application.exception.ApplicationNotAvailableException;
import com.example.party.application.exception.ApplicationNotGeneraleException;
import com.example.party.application.repository.ApplicationRepository;
import com.example.party.application.type.ApplicationStatus;
import com.example.party.partypost.entity.PartyPost;
import com.example.party.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ApplicationValidator {

	private final ApplicationRepository applicationRepository;

	//참가신청 작성 전 조건 검증 메소드
	public void validationApplicationBeforeCreation(PartyPost partyPost, User user) {
		//(1) 내가 작성자인지 확인
		if (partyPost.isWrittenByMe(user.getId())) {
			throw new ApplicationNotGeneraleException("내가 작성한 모집글에 참가신청할 수 없습니다");
		}

		//(2) partyPost 가 모집마감시간전인지 확인
		if (!partyPost.beforeCloseDate(LocalDateTime.now())) {
			throw new ApplicationNotGeneraleException("모집 마감시간이 지나, 참가신청할 수 없습니다");
		}
		//(3) partyPost 가 FINDING 인지 확인
		if (!partyPost.isFinding()) {
			throw new ApplicationNotGeneraleException("모집글이 모집 중인 상태가 아닙니다");
		}

		//(4) 중복검사
		if (applicationRepository.existsByPartyPostAndUser(partyPost, user)) {
			throw new ApplicationNotGeneraleException("이미 신청한 모집글입니다");
		}
	}

	//참가신청이 PENDING(대기중) 상태인지 확인
	public void validateApplicationStatus(Application application) {
		if (application.getStatus() != ApplicationStatus.PENDING) {
			throw new ApplicationNotAvailableException();
		}
	}
}
