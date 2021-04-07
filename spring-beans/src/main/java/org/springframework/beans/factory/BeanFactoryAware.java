/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.beans.BeansException;

/**
 * 由希望知道自己的{@link BeanFactory}的bean实现的接口。
 * <p>例如，bean可以通过工厂（Dependency Lookup）来查找协作bean。
 * 注意，大多数bean将选择通过相应的bean属性或构造函数参数（依赖注入）来接收对协作bean的引用。
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * 将拥有的工厂提供给Bean实例的回调。
	 * <p>在填充正常的bean属性之后但在初始化回调（例如{@link InitializingBeanafterPropertiesSet（）}或自定义的init-method）之前调用。
	 * @param beanFactory拥有BeanFactory（永远{@code null}）。
	 *        Bean可以立即在工厂中调用方法。
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
