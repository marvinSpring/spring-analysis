//package com.marvin.test.self.bfpp;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.context.support.AbstractApplicationContext;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//
////TODO:这种情况spring好像不会重新加载bfpp，但是bdrpp可以，留意下 有可能是spring的bug
//@Component
//public class MarvinBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
//
//
//	@Override
//	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//		if (beanFactory instanceof DefaultListableBeanFactory){
//			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MarvinExtendsBeanFactoryPostProcessor2.class);
//			BeanDefinitionBuilder beanDefinitionBuilder2 = BeanDefinitionBuilder.rootBeanDefinition(MarvinExtendsBeanFactoryPostProcessor1.class);
//
//			((DefaultListableBeanFactory)beanFactory).registerBeanDefinition("MarvinExtendsBeanFactoryPostProcessor2",beanDefinitionBuilder.getBeanDefinition());
//			((DefaultListableBeanFactory)beanFactory).registerBeanDefinition("MarvinExtendsBeanFactoryPostProcessor1",beanDefinitionBuilder2.getBeanDefinition());
//		}else {
//			System.out.println("gan!!!");
//		}
//	}
//
//	@Override
//	public int getOrder() {
//		return 0;
//	}
//}
