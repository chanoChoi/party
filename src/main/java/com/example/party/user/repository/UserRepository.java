package com.example.party.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.party.user.dto.ProfileRequest;
import com.example.party.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Boolean existsUserByEmail(String email);

	Boolean existsUserByNickname(String nickname);

	List<User> findAllByNicknameIn(List<String> nicknames);

	@Query(value = "SELECT u FROM User u WHERE u.status = 'SUSPENDED'")
	List<User> statusEqualSuspended(Pageable pageable);

	@Query(value = "SELECT u FROM User u WHERE u.profile.noShowCnt >= 1 ")
	List<User> findAllByNoShowList(Pageable pageable);

	Optional<User> findByKakaoId(Long id); //카카오 Oauth2 용
}
