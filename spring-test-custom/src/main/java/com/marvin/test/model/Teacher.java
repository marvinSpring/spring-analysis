package com.marvin.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Teacher {

	private Student student;

	@Override
	public String toString() {
		return "Teacher{" +
				"student=" + student +
				'}';
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
}
