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

package org.springframework.beans.factory;

/**
 * 由{@link BeanFactory}设置完所有属性后需要响应的bean所实现的接口：
 * 执行自定义初始化，或仅检查是否已设置所有必填属性。
 * <p>实现{@code InitializingBean}的另一种方法是指定一个自定义的init方法，
 * 例如在XML bean定义中。有关所有bean生命周期方法的列表，请参见{@link BeanFactory BeanFactory javadocs}。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see DisposableBean
 * @see org.springframework.beans.factory.config.BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getInitMethodName()
 */
public interface InitializingBean {

	/**
	 * 由包含的{@code BeanFactory}设置所有bean属性并满足{@link BeanFactoryAware}，
	 * {@code ApplicationContextAware}等之后调用
	 * 。<p>此方法允许bean实例执行其整体配置的验证和最终初始化。
	 * 设置了所有bean属性后。
	 * @throws 在配置错误（例如无法设置基本属性）或初始化由于任何其他原因而失败的情况下发生的异常
	 */
	void afterPropertiesSet() throws Exception;

}
