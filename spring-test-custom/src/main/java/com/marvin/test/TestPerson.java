package com.marvin.test;

import com.marvin.test.model.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestPerson {

	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		Person person = (Person) ac.getBean("person");
		System.out.println(person);
	}
}
