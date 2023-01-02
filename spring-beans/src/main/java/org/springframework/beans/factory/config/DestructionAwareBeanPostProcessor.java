/*
 * Copyright 2002-2018 the original author or authors.
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

/**
 * 在bean的大的处理流程全部完成,但是在destroy方法之前调用,一般可以用于一些连接或者流的关闭
 *
 * {@link BeanPostProcessor} 的子接口，用于添加销毁前回调。
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * 在销毁之前将此 BeanPostProcessor 应用于给定的 Bean 实例，例如调用自定义销毁回调。
	 * <p>与 DisposableBean 的 {@code 销毁} 和自定义销毁方法一样，此回调仅适用于容器完全管理生命周期的 bean。
	 * 单例和范围 bean 通常就是这种情况。@param Bean 要销毁的 Bean 实例
	 * @param beanName the name of the bean
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName(String)
	 */
	//执行销毁方法之前的自定义销毁方法
	void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

	/**
	 * 确定给定的 Bean 实例是否需要此后处理器销毁。
	 * <p>默认实现返回 {@code true}。
	 * 如果 {@code DestructionAwareBeanPostProcessor} 的 pre-5 实现没有提供此方法的具体实现，
	 * Spring 也会默默地假设 {@code true}。
	 * @param bean the bean instance to check
	 * @return {@code true} if {@link #postProcessBeforeDestruction} is supposed to
	 * be called for this bean instance eventually, or {@code false} if not needed
	 * @since 4.3
	 */
	//用于在真正的执行销毁方法 之前判断是否真正需要被销毁,spring默认是需要的
	default boolean requiresDestruction(Object bean) {
		return true;
	}

}
