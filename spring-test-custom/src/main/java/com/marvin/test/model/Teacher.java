package com.marvin.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

public class Teacher {
	@Override
	public String toString() {
		return "Teacher{" +
				"age='" + age + '\'' +
				'}';
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	/*private Student student;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}*/

	private String age;
}
