package com.marvin.test.model;

import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.core.env.Environment;

public class Dog implements ApplicationContextAware, EnvironmentAware {

	private ApplicationContext applicationContext;

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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setEnvironment(Environment environment) {

	}
}
