package com.example.party.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.party.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	//카테고리 조회
	List<Category> findAllByActiveIsTrue();

	Optional<Category> findById(Long Id);

	boolean existsCategoryByName(String name);
}
