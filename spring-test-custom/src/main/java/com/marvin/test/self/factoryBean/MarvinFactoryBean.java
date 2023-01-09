/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.self.factoryBean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class MarvinFactoryBean implements FactoryBean<Marvin>, InitializingBean {

	private final ConcurrentMap<String,Marvin> marvinConcurrentMap = new ConcurrentHashMap<>();

	@Override
	public Marvin getObject() throws Exception {
		synchronized (marvinConcurrentMap) {
			if (marvinConcurrentMap.size() > 0) {
				return marvinConcurrentMap.values().stream().findFirst().get();
			}
			Class<Marvin> marvinClass = Marvin.class;
			Constructor<?>[] constructors = marvinClass.getDeclaredConstructors();
			Constructor<?> constructor1 = null;
			for (Constructor<?> constructor : constructors) {
				constructor.setAccessible(true);
				constructor1 = constructor;
			}
			Marvin marvin = (Marvin) constructor1.newInstance();
			Field field = marvinClass.getDeclaredField("name");
			field.setAccessible(true);
			field.set(marvin, "marvin");
			return marvin;
		}
	}


	@Override
	public Class<?> getObjectType() {
		return Marvin.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		marvinConcurrentMap.put("hhMarvin",getObject());
	}
}
