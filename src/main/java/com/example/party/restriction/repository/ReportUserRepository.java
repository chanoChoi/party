package com.example.party.restriction.repository;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.party.restriction.entity.ReportUser;

public interface ReportUserRepository extends JpaRepository<ReportUser, Long> {
	Boolean existsByReporterIdAndReportedId(Long reporterId, Long reportedId);

	//유저 신고 로그 조회
	List<ReportUser> findAllByOrderById(Pageable pageable);
}
