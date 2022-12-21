package com.marvin.test.self.edtior.entity;

public class User {

	private String userId;

	private Marvin marvin;

	@Override
	public String toString() {
		return "User{" +
				"userId='" + userId + '\'' +
				", marvin=" + marvin +
				'}';
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Marvin getMarvin() {
		return marvin;
	}

	public void setMarvin(Marvin marvin) {
		this.marvin = marvin;
	}
}
