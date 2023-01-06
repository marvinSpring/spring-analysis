/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.annocollect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 包装类
 */
public class ClassHolder {

	//当前拜访的类
	private Class<?> clazz;

	private List<Annotation> annotations;

	//@ActuallyAnno注解身上的值
	private String value;

	/**
	 * 获取当前注解身上的所有注解
	 * @return 包装类
	 */
	public Set<ClassHolder> getAnnos(){
		return Arrays.stream(clazz.getAnnotations()).map(x->{
			ClassHolder classHolder = new ClassHolder();
			List<Annotation> annotations = new ArrayList<>(clazz.getAnnotations().length);
			annotations.add(x);
			classHolder.setAnnotations(annotations);
			classHolder.setClazz(x.getClass());
			try {
				for (Field field : x.getClass().getFields()) {
					classHolder.value = (String) field.get("value");
				}
			} catch (IllegalAccessException e) {
			}
			return classHolder;
		}).collect(Collectors.toSet());
	}

	public static void main(String[] args) {

	}


	private List<Annotation> getAnnotations() {
		return annotations;
	}

	private void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Collection<? extends ClassHolder> getAnnoAttributes(String name, String value) {
		return null;
	}
}
