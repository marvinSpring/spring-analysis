package com.marvin.test.self.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class MarvinAwarePostProcessor implements BeanPostProcessor {

	private final ConfigurableApplicationContext applicationContext;

	public MarvinAwarePostProcessor(ConfigurableApplicationContext applicationContext) {
		if (applicationContext instanceof  AbstractApplicationContext) {
			((AbstractApplicationContext) applicationContext).setDisplayName("imOK");
		}
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		invokeAwareInterface(bean);
		return bean;
	}

	public void invokeAwareInterface(Object bean){
		if (bean instanceof ApplicationContextAware){
			((ApplicationContextAware) bean).setApplicationContext(this.getApplicationContext());
		}
	}

	public ConfigurableApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
