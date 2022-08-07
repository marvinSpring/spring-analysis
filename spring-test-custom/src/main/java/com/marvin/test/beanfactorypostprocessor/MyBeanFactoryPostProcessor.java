//package com.marvin.test.beanfactorypostprocessor;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//
//public class MyBeanFactoryPostProcessor implements BeanPostProcessor {
//
//	@Override
//	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//		return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
//	}
//
//	@Override
//	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//	}
//}
