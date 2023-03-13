package com.example.party.restriction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.party.restriction.entity.NoShow;

public interface NoShowRepository extends JpaRepository<NoShow, Long> {
	Boolean existsByReporterIdAndReportedIdAndPartyPostId(Long reporterId, Long reportedId, Long partyPostId);

	List<NoShow> findAllByPartyPostId(Long partyPostId);
}
