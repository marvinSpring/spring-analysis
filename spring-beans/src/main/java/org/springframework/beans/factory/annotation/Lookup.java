/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示“查找”方法的注释，将被容器覆盖，
 * 以将它们重定向回 {@link org.springframework.beans.factory.BeanFactory}
 * 以进行 {@code getBean} 调用。
 * 这实质上是基于注解的 XML {@code lookup-method} 属性版本，导致相同的运行时安排。
 *
 * <p>目标 Bean 的解析可以基于返回类型 （{@code getBean（Class）}）
 * 或建议的 Bean 名称 （{@code getBean（String）}），
 * 在这两种情况下，都将方法的参数传递给 {@code getBean} 调用，
 * 以将它们作为目标工厂方法参数或构造函数参数应用。
 *
 * <p>此类查找方法可以具有默认（存根）实现，这些实现将被容器替换，
 * 也可以将它们声明为抽象 - 供容器在运行时填充它们。
 * 在这两种情况下，容器都将通过 CGLIB 生成方法包含类的运行时子类，
 * 这就是为什么此类查找方法只能处理容器通过常规构造函数实例化的 bean：
 * 即查找方法不能替换从工厂方法返回的 bean，我们无法动态地为它们提供子类。
 *
 * <p>
 *     <b>针对典型 Spring 配置场景的建议：
 *     <b>当在某些情况下可能需要具体类时，请考虑提供查找方法的存根实现。
 *     请记住，查找方法不适用于从配置类中的 {@code @Bean} 方法返回的 bean;
 *     您必须求助于 {@code @Inject提供程序<TargetBean>} 或类似方法
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.BeanFactory#getBean(Class, Object...)
 * @see org.springframework.beans.factory.BeanFactory#getBean(String, Object...)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lookup {

	/**
	 * 此注解属性可能会建议查找目标 Bean 名称。
	 * 如果未指定，将根据带注解的方法的返回类型声明解析目标 Bean。
	 */
	String value() default "";

}
