package com.marvin.test.self.tags.entity;

public class Marvin {

	private String username;

	private String password;

	private Integer age;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Marvin{" +
				"username='" + username + '\'' +
				", password='" + password + '\'' +
				", age=" + age +
				'}';
	}
}
