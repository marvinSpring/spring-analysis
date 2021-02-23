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

/**
 * 允许自定义修改应用程序上下文的Bean定义，以适应上下文基础bean工厂的Bean属性值。
 * <p>ApplicationContext可以在其Bean定义之前自动检测（探测）BeanFactoryPostProcessor Bean，并在创建Bean之前应用它们。
 * <p>对于针对系统管理员的自定义配置文件很有用，这些文件覆盖了在应用程序上下文中配置的Bean属性。
 *
 * <p>请参见PropertyResourceConfigurer及其具体实现，
 * 以获取可解决此类配置需求的Spring容器外的解决方案——通过pc读取properties信息注入。
 *
 * <p>BeanFactoryPostProcessor可以与Bean定义进行交互并对其进行修改，
 * 但不能与Bean实例进行交互。这样做可能会导致bean实例化过早，
 * 从而违反了容器并造成了意外的副作用。如
 * 果需要与bean实例交互，请考虑改为实现{@link BeanPostProcessor}。
 *
 * @author Juergen Hoeller
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * 标准初始化后，修改应用程序上下文的内部bean工厂。
	 * 所有bean定义都将被加载，但是尚未实例化任何bean。这甚至可以覆盖或添加属性，甚至可以用于初始化bean。
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
