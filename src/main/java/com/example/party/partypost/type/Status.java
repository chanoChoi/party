package com.example.party.partypost.type;

public enum Status {
	FINDING, //파티원 모집중
	FOUND, //모집마감 (인원이 다 찬 경우) > [마감시간이 되면 노쇼체크 가능]으로 상태 변경
	NO_SHOW_REPORTING, //노쇼체크가 가능기간 = ( 마감시간 ~ 모임시간 + 1시간 )
	PROCESSING, //노쇼체크 결산
	END //종료 [1](인원이 차지 않은경우) & 종료 [2](모임의 노쇼체크완료된)
}
