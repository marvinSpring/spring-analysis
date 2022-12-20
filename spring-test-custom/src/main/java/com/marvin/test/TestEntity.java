package com.marvin.test;

import org.springframework.stereotype.Component;

@Component
public class TestEntity {

	public Long id;

	@Override
	public String toString() {
		return "BaseEntity{" +
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
