package com.marvin.test.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class Student {

	private Teacher teacher;

	@Override
	public String toString() {
		return "Student{" +
				"teacher=" + teacher +
				'}';
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
}
