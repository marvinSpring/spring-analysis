package com.marvin.test;

import com.marvin.test.controller.TestController;
import com.marvin.test.model.MyClassPathXmlApplicationContext;
import com.marvin.test.model.StudentService;
import com.marvin.test.model.Teacher;
import com.marvin.test.service.TestService;
import org.springframework.beans.factory.BeanFactory;
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
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
		ac.register(TestService.class);
		ac.register(TestController.class);
		ac.refresh();
		TestController testController = ac.getBean("testController", TestController.class);
		testController.test();
	}

}
