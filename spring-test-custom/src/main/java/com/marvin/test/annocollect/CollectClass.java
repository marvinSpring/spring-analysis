/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.annocollect;

import java.lang.annotation.Annotation;
import java.util.*;

public class CollectClass {

	/**
	 * 递归收集 {@link  ActuallyAnno} 注解身上的所有value
	 * @param collectedActuallyAnnoValues 当前收集到 注解{@link  ActuallyAnno}中的value们
	 * @param currentVisitedAnno 当前正在处于的注解中
	 * @param needCheckIsHaveActuallyAnno 需要检查是否包含{@link  ActuallyAnno}这个注解的类
	 */
	public static void testCollectActuallyAnno2(Set<String> collectedActuallyAnnoValues, Set<Object> currentVisitedAnno, Object needCheckIsHaveActuallyAnno){
		try {
			if (currentVisitedAnno.add(needCheckIsHaveActuallyAnno)){
				Class<?> aClass = needCheckIsHaveActuallyAnno.getClass();
				//emm,刚发现Java的注解都被JDKProxy代理走了,
				// 所以这里需要判断拿到的注解类是不是被代理了,如果被代理了,
				// 就获取代理类实现的接口,因为JDK是通过实现被代理对象的接口生成的子类来代理的，
				// 这里如果是被cglib所代理,那么就应该获取该代理类继承的父类,也就是superClass来获取到被代理类的Class对象
				if (aClass.getName().startsWith("com.sun.proxy")){
					aClass = Class.forName(((Class<?>) aClass.getGenericInterfaces()[0]).getName());
				}
				for (Annotation annotation : aClass.getAnnotations()) {
					if (!(annotation instanceof ActuallyAnno)){
						testCollectActuallyAnno2(collectedActuallyAnnoValues,currentVisitedAnno,annotation);
					}else {
						collectedActuallyAnnoValues.addAll(Collections.singletonList(annotation.toString()));
					}
				}
			}
		} catch (Exception ignored) {
			// FIXME: 2023/1/2 无需抛出异常,因为这里没有啥意义,上面的反射引起的异常肯定很多,引起异常了,那就递归到上一层算了,暂时这么处理
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class<TestClass> aClass = TestClass.class;
		HashSet<String> collectedActuallyAnnoValues = new HashSet<>();
		HashSet<Object> currentVisitedAnno = new HashSet<>();
		testCollectActuallyAnno2(collectedActuallyAnnoValues,currentVisitedAnno,new TestClass());
		System.out.println(collectedActuallyAnnoValues);
		System.out.println(currentVisitedAnno);

	}
}
