package com.marvin.test.model;

public class People {
	@Override
	public String toString() {
		return "People{" +
				"name='" + name + '\'' +
				", dog=" + dog +
				'}';
	}

	public People() {
	}

	public People(String name, Dog dog) {
		this.name = name;
		this.dog = dog;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Dog getDog() {
		return dog;
	}

	public void setDog(Dog dog) {
		this.dog = dog;
	}

	private String name;

	private Dog dog;
}
