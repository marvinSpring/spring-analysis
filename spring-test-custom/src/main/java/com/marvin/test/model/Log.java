package com.marvin.test.model;

import org.springframework.stereotype.Component;

@Component
public class Log {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String name;
}
