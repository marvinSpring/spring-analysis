/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * 工厂挂钩允许自定义修改新的bean实例，例如检查标记界面或使用代理包装它们。
 *
 * <p>ApplicationContexts可以在其Bean定义中自动检测BeanPostProcessor Bean，
 * 并将其应用于随后创建的任何Bean。
 * 普通bean工厂允许对后处理器进行程序化注册，适用于通过该工厂创建的所有bean。
 *
 * <p>通常，通过标记器接口等填充bean的后处理器将实现{@link this#postProcessBeforeInitialization}，
 * 而使用代理包装bean的后处理器通常将实现{@link this#postProcessAfterInitialization}。
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * 在任何bean初始化回调（如InitializingBean的{@code afterPropertiesSet}或自定义的初始化方法）之前，
	 * 将此BeanPostProcessor应用于给定的新bean实例
	 * <i>该bean将已经用属性值填充。
	 * 返回的Bean实例可能是原始实例的包装。
	 * <p>默认实现按原样返回给定的{@code bean}。
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	//在bean实例化之后、初始化之前调用该方法
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 *将此BeanPostProcessor应用于给定的新bean实例
	 * 之后任何bean初始化回调（例如InitializingBean的{@code afterPropertiesSet}或自定义的init-method）。
	 * 该bean将已经用属性值填充。返回的Bean实例可能是原始实例的包装。
	 * <p>如果是FactoryBean，则将为FactoryBean实例和由FactoryBean创建的对象（从Spring 2.0开始）调用此回调。
	 * 后处理器可以通过相应的{@code bean instanceof FactoryBean}检查来决定是应用到FactoryBean还是创建的对象，还是两者都应用。
	 * <p>This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	//在bean注册destroy之前、实例化之后调用该方法
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
