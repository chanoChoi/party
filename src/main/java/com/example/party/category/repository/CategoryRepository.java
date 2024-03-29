package com.example.party.category.repository;

import java.util.List;
import java.util.Optional;

import com.example.party.category.entity.Category;

public interface CategoryRepository {
	List<Category> findAllByActiveIsTrue();

	Optional<Category> findById(Long Id);

	boolean existsCategoryByName(String name);
	Category save(Category category);
}
