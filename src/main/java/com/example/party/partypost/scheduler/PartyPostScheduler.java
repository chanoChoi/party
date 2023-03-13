package com.example.party.partypost.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.party.partypost.repository.PartyPostRepository;
import com.example.party.restriction.service.RestrictionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class PartyPostScheduler {
	private final RestrictionService restrictionService;
	private final PartyPostRepository partyPostRepository;

	// @Scheduled(cron = "0 45 * * * ?")// 실제 업로드용(매 시 45분마다 실행)
	// @Scheduled(cron = "0 0 * * * ?") // 실제 업로드용(매 시 정각마다 실행)
	// @Scheduled(cron = "0 * * * * ?") // 매 1분 간격으로 실행(테스트용)

	//FOUND -> NO_SHOW_REPORTING (모임시작시간이 되면 변경)
	@Scheduled(cron = "0 0 * * * ?")//(테스트용)1분 간격으로 실행 // 배포시 매시 정각마다로 변경 필요
	public void changeStatusFoundToNoShow() {
		System.out.println("FOUND -> NO_SHOW_REPORTING 동작");
		partyPostRepository.changeStatusFoundToNoShow();
	}

	//FINDING -> END (모집마감시간이 됐는데도 아직 FINDING 상태일 때)
	@Scheduled(cron = "0 45 * * * ?") //(테스트용)1분 간격으로 실행 // 배포시 매시 45분 마다로 변경 필요
	public void changeStatusFindingToProcessing() {
		System.out.println("FINDING -> END 동작");
		partyPostRepository.changeStatusFindingToProcessing();
	}

	// NO_SHOW_REPORTING -> PROCESSING (모임시간 후 1시간 지나면 변경)
	@Scheduled(cron = "0 0 * * * ?") // (테스트용)1분 간격으로 실행 // 배포시 매시 정각 마다로 변경 필요
	public void changeStatusNoShowToProcessing() {
		System.out.println("NO_SHOW_REPORTING -> PROCESSING 동작");
		partyPostRepository.changeStatusNoShowToProcessing(LocalDateTime.now().minusHours(1));
		restrictionService.checkingNoShow(partyPostRepository.statusEqualProcessing());
	}
}
