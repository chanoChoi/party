package com.example.party.category.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.party.category.dto.CategoryRequest;
import com.example.party.global.common.TimeStamped;
import com.example.party.partypost.entity.PartyPost;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity(name = "category")
public class Category extends TimeStamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	@Column(name = "active", nullable = false)
	private Boolean active;

	@OneToMany(mappedBy = "category")
	private List<PartyPost> partyPostList;

	//생성자
	public Category(CategoryRequest request) {
		this.name = request.getName();
		this.active = true;
	}

	//카테고리 수정
	public void update(CategoryRequest request) {
		this.name = request.getName();
	}

	public void deleteCategory() {
		this.active = false;
	}

	public boolean isActive() {
		return this.active;
	}
}
