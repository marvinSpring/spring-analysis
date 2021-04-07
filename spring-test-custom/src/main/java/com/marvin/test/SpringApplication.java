package com.marvin.test;

import com.marvin.test.model.MyClassPathXmlApplicationContext;
import com.marvin.test.model.StudentService;
import com.marvin.test.model.Teacher;
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
//    	BeanFactory ac = new MyClassPathXmlApplicationContext("applicationContext.xml");
//		Teacher teacher = ac.getBean("teacher", Teacher.class);
		BeanFactory ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		ac.getBean("teacher");
	}
}
