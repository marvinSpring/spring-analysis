package com.marvin.test.self.bfpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

public class MarvinBeanFactoryPostProcessor implements BeanFactoryPostProcessor {


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof AbstractApplicationContext){
			System.out.println(((AbstractApplicationContext)beanFactory).getApplicationName());
		}else {
			System.out.println("gan!!!");
		}
	}
}
