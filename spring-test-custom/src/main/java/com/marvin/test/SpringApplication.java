package com.marvin.test;

import com.marvin.test.controller.TestController;
import com.marvin.test.model.*;
import com.marvin.test.service.TestService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Marvin
 */
@ComponentScan("com.marvin.test.*")
@EnableAspectJAutoProxy
public class SpringApplication {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = new MyClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(applicationContext.getBean("testEntity"));
//		ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.marvin.test.*");
//		System.out.println(applicationContext.getBean(Log.class));
//		ApplicationContext beanFactory = new ClassPathXmlApplicationContext("spring-${username}.xml");
//		Dog dog = beanFactory.getBean("dog", Dog.class);
//		ApplicationContext applicationContext = dog.getApplicationContext();
//		System.out.println(applicationContext);
	}

}
