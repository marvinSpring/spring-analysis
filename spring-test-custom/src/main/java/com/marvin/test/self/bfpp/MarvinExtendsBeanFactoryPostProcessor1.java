///*
// * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// * Vestibulum commodo. Ut rhoncus gravida arcu.
// */
//
//package com.marvin.test.self.bfpp;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.core.PriorityOrdered;
//
////TODO:这种情况spring好像不会重新加载bfpp，但是bdrpp可以，留意下 有可能是spring的bug
//public class MarvinExtendsBeanFactoryPostProcessor1 implements BeanFactoryPostProcessor, PriorityOrdered {
//
//	@Override
//	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		System.out.println("im one");
//	}
//
//	@Override
//	public int getOrder() {
//		return 0;
//	}
//}
