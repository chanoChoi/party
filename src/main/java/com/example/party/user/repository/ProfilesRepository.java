package com.example.party.user.repository;

import com.example.party.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfilesRepository extends JpaRepository<Profile, Long> {
    // boolean existsProfileBy(String profile); jpa 메소드명 작성 규칙에 대해 알아봐주세요.
}
