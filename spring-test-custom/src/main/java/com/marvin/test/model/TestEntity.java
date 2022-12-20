package com.marvin.test.model;

import org.springframework.stereotype.Component;

@Component
public class TestEntity   {

	private Long id;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TestEntity() {
	}

	public TestEntity(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "TestEntity{" +
				"id=" + id +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
