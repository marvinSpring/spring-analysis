package com.marvin.test.self.bfpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.PropertyValuesEditor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

@Component
public class MarvinBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("postProcessBeanFactory...");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		System.out.println("ppbdr...");
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MarvinExtendsBeanDefinitionRegistryPostProcessor.class);
		MutablePropertyValues propertyValues = new MutablePropertyValues();
		propertyValues.add("name","123456");
		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		beanDefinition.setPropertyValues(propertyValues);
		registry.registerBeanDefinition("marvinExtendsBeanDefinitionRegistryPostProcessor",beanDefinition);
	}

}
