package com.marvin.test.self.edtior.entity;

public class Marvin {

	private String firstUsername;

	private String secondUsername;

	@Override
	public String toString() {
		return "Marvin{" +
				"firstUsername='" + firstUsername + '\'' +
				", secondUsername='" + secondUsername + '\'' +
				'}';
	}

	public String getFirstUsername() {
		return firstUsername;
	}

	public void setFirstUsername(String firstUsername) {
		this.firstUsername = firstUsername;
	}

	public String getSecondUsername() {
		return secondUsername;
	}

	public void setSecondUsername(String secondUsername) {
		this.secondUsername = secondUsername;
	}
}
