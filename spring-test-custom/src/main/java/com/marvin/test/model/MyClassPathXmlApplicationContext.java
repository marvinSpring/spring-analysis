package com.marvin.test.model;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class MyClassPathXmlApplicationContext extends ClassPathXmlApplicationContext {

	public MyClassPathXmlApplicationContext(String... locations) throws BeansException {
		super(locations);
	}

	@Override
	protected void initPropertySources() {
		System.out.println("自定义扩展，初始化属性资源");
		getEnvironment().setRequiredProperties("java.xxx.aaa");
		setAllowBeanDefinitionOverriding(false);
	}



	/*	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		beanFactory.getBeanDefinition("teacher").setLazyInit(true);
		System.out.println(666);
	}

	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		beanFactory.setAllowCircularReferences(true);
		beanFactory.setAllowBeanDefinitionOverriding(beanFactory.isAllowBeanDefinitionOverriding());
	}*/
}
