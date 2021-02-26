package com.marvin.test.model;

import org.springframework.context.ApplicationContext;

public class Testdsa {

	public static void main(String[] args) {
		ApplicationContext ac = new MyClassPathXmlApplicationContext("applicationContext.xml");
		Teacher teacher = ac.getBean("teacher",Teacher.class);
		int a = 0;
	}

}
