package com.example.party.partypost.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.party.partypost.entity.PartyPost;

public interface PartyPostRepository extends JpaRepository<PartyPost, Long> {

	//모집글 전체 조회
	List<PartyPost> findAllByActiveIsTrue(Pageable pageable);

	// postId 로 특정 모집글 가져오기
	Optional<PartyPost> findByIdAndActiveIsTrue(Long postId);

	// userId 로 active된 특정 모집글 가져오기
	List<PartyPost> findByUserIdAndActiveIsTrue(Long userId, Pageable pageable);

	//내가 작성한 모집글 리스트 조회
	List<PartyPost> findByUserId(Long userId, Pageable pageable);

	List<PartyPost> findAllByUserId(Long userId);

	@Modifying
	@Query(value = "SELECT p FROM partyPost p WHERE p.status = 'PROCESSING'")
	List<PartyPost> statusEqualProcessing();

	// FOUND -> NO_SHOW_REPORTING (모임시작시간이 되면 변경)
	@Modifying
	@Query(value = "UPDATE partyPost p SET p.status = 'NO_SHOW_REPORTING' WHERE p.active = true AND p.status = 'FOUND' AND p.partyDate<=CURRENT_TIMESTAMP")
	void changeStatusFoundToNoShow();

	//NO_SHOW_REPORTING -> PROCESSING (모임시간 후 1시간 지나면 변경)
	@Modifying
	@Query(value = "UPDATE partyPost p SET p.status = 'PROCESSING' WHERE p.active = true AND p.status = 'NO_SHOW_REPORTING' AND p.partyDate<=:beforeHourFromNow")
	void changeStatusNoShowToProcessing(LocalDateTime beforeHourFromNow);

	//FINDING -> END (마감시간이 됐는데도 FINDING 상태면 END 으로 변경)
	@Modifying
	@Query(value = "UPDATE partyPost p SET p.status = 'END' WHERE p.active = true AND p.status = 'FINDING' AND p.partyDate<=CURRENT_TIMESTAMP")
	void changeStatusFindingToProcessing();

	//제목 혹은 주소 값이 검색문자에 포함시에 모집글 리스트 조회
	List<PartyPost> findByTitleContainingOrAddressContaining(String title, String address, Pageable pageable);

	//조회수가 많은 모집글 리스트 조회
	List<PartyPost> findFirst3ByOrderByViewCntDesc(Pageable pageable);

	//categoryId 로 모집글 리스트 조회
	List<PartyPost> findByCategoryId(Long categoryId, Pageable pageable);

	//categoryId 로 active된 모집글 리스트 조회
	List<PartyPost> findByCategoryIdAndActiveIsTrue(Long categoryId, Pageable pageable);

	// 읍면동으로 모집글 리스트 조회
	List<PartyPost> findByAddressContaining(String place, Pageable pageable);
}
