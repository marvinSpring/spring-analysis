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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 运行时
 * <i>合并 Bean 定义的后处理器回调接口<i>。
 * {@link BeanPostProcessor} 实现可以实现此子接口，
 * 以便对 Spring {@code BeanFactory} 用于创建 Bean 实例的合并 Bean 定义（原始 Bean 定义的处理副本）进行后处理。
 *
 * <p>例如，{@link postProcessMergedBeanDefinition} 方法可以内省 Bean 定义，
 * 以便在对 Bean 的实际实例进行后处理之前准备一些缓存的元数据。
 * 还允许修改 Bean 定义，
 * 但
 * <i>仅限于<i>实际用于并发修改的定义属性。本质上，
 * 这仅适用于在 {@link RootBeanDefinition} 本身上定义的操作，而不适用于其基类的属性。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * 对指定 Bean 的给定合并 Bean 定义进行后置增强处理。
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 * @see AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors
	 */
	//通过本方法,Spring容器可以找出所有需要注入的字段,同时做一个缓存
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

	/**
	 * 通知指定名称的 Bean 定义已重置，并且此后处理器应清除受影响 Bean 的所有元数据。
	 * <p>默认实现为空。
	 * @param beanName the name of the bean
	 * @since 5.1
	 * @see DefaultListableBeanFactory#resetBeanDefinition
	 */
	//用于在beanDefinition被修改后,清除容器的缓存
	default void resetBeanDefinition(String beanName) {
	}

}
