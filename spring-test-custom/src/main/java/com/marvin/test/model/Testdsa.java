package com.marvin.test.model;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Testdsa {
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		Teacher teacher = ac.getBean("teacher",Teacher.class);
		System.out.println(teacher);
	}
}
