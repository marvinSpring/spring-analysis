package com.marvin.test.model;

public class Dog {

	private String size;

	@Override
	public String toString() {
		return "Dog{" +
				"size='" + size + '\'' +
				'}';
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Dog() {
	}

	public Dog(String size) {
		this.size = size;
	}
}
