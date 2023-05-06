/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

/**
 * 本节课接口为共享bean实例定义注册中心。
 * 可以通过{@link org.springframework.beans.factory.BeanFactory}实现。以便以统一的方式公开处理它们的单例管理功能。
 *
 * <p>可配置的bean工厂 {@link ConfigurableBeanFactory}扩展了这个接口。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ConfigurableBeanFactory
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
 * @see org.springframework.beans.factory.support.AbstractBeanFactory
 */
public interface SingletonBeanRegistry {

	/**
	 * 在bean注册中心中，在给定的bean名称下，将给定的现有对象注册为单例对象。
	 *
	 * <p>给定的实例应该是完全初始化的;
	 * 注册表将不会执行任何初始化回调(特别是，它不会调用InitializingBean的{@code afterPropertiesSet}方法)。
	 *
	 * 给定的实例也不会接收任何销毁回调(就像DisposableBean的{@code destroy}方法)。
	 *
	 * <p>当在一个完整的BeanFactory中运行时:
	 *     <b>如果你的bean应该接收初始化和或销毁回调，那么注册一个bean定义而不是一个现有的实例
	 * <p>通常在注册表配置期间调用，但也可以用于单例的运行时注册。
	 * 因此，注册表实现应该同步单例访问;
	 * 如果它支持BeanFactory对单例对象的懒加载实例化，那么无论如何它都必须这样做。
	 *
	 * @param beanName bean名称
	 * @param singletonObject 现有的单例对象
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.DisposableBean#destroy
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#registerBeanDefinition
	 */
	//将给定的bean名称和对象注册到bean的注册中心中
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * 返回在给定名称下注册的(原始)单例对象。
	 *
	 * <p>只检查已经实例化的单例;
	 * 对于尚未实例化的单例bean定义，不会返回Object。
	 *
	 * <p>这个方法的主要目的是访问手动注册的单例对象(参见{@link #registerSingleton})。
	 * 还可以用于以原始方式访问由已经创建的bean定义定义的单例。
	 *
	 * <p><b>注意:</b> 此查找方法不知道FactoryBean前缀或别名。在获得单例实例之前，您需要首先解析规范bean名称。
	 *
	 * @param beanName 要查找的bean的名称
	 * @return 已注册的单例对象，如果没有找到则为{@code null}
	 * @see ConfigurableListableBeanFactory#getBeanDefinition
	 */
	@Nullable
	Object getSingleton(String beanName);

	/**
	 * 检查此注册表是否包含具有给定名称的单例实例。
	 *
	 * <p>只检查已经实例化后的单例;
	 * 对于尚未实例化的单例bean定义不返回{@code true}。
	 *
	 * <p>这个方法的主要目的是检查手动注册的单例(参见{@link #registerSingleton})。
	 * 还可以用于检查bean定义定义的单例是否已经创建。
	 *
	 * <p>要检查bean工厂是否包含具有给定名称的bean定义，请使用ListableBeanFactory的{@code containsBeanDefinition}。
	 * 调用{@code containsBeanDefinition}和{@code containsSingleton}可以回答特定的bean工厂是否包含具有给定名称的本地bean实例。
	 *
	 * <p>使用BeanFactory的{@code containsBean}来检查工厂是否知道具有给定名称的bean(无论是手动注册的单例实例还是由bean定义创建的)，
	 * 还可以检查有层次的祖先级工厂。
	 *
	 * <p><b>注意:</b> 此查找方法不知道FactoryBean前缀或别名。在检查单例状态之前，您需要首先解析规范bean名称。
	 * @param beanName 要查找的bean名称
	 * @return 如果此bean工厂包含具有给定名称的单例实例
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition
	 * @see org.springframework.beans.factory.BeanFactory#containsBean
	 */
	boolean containsSingleton(String beanName);

	/**
	 * 返回在此注册中心中注册的单例bean的名称。 
	 * <p>只检查已经实例化的单例;不返回尚未实例化的单例bean定义的名称。
	 *
	 * <p>这个方法的主要目的是检查手动注册的单例(@see {@link #registerSingleton})。
	 * 还可以用于检查bean定义的单例是否已经创建。
	 *
	 * @return 将名称列表作为字符串数组(never {@code null})
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionNames
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionNames
	 */
	String[] getSingletonNames();

	/**
	 * 返回在此注册中心中注册的单例bean的数量。
	 *
	 * <p>只检查已经实例化的单例;
	 * 不计算尚未实例化的单例bean定义。
	 *
	 * <p>这个方法的主要目的是检查手动注册的单例(参见{@link #registerSingleton})。
	 * 还可以用来计算已经创建的bean定义定义的单例的数量。
	 *
	 * @return 在bean的注册中心的单例bean的数量
	 * @see #registerSingleton
	 * @see org.springframework.beans.factory.support.BeanDefinitionRegistry#getBeanDefinitionCount
	 * @see org.springframework.beans.factory.ListableBeanFactory#getBeanDefinitionCount
	 */
	int getSingletonCount();

	/**
	 * 返回此注册表使用的单例互斥锁(用于外部协作者)。
	 * @return 互斥对象(从不 {@code null})
	 * @since 4.2
	 */
	Object getSingletonMutex();

}
