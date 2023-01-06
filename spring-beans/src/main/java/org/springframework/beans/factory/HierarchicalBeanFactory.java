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

import org.springframework.lang.Nullable;

/**
 * 本接口拥有 层次结构的 bean工厂
 *
 * 由bean工厂实现的子接口，可以是层次结构的一部分。
 *
 * <p>可以在ConfigurableBeanFactory接口中找到用于bean工厂
 * 的相应{@code setParentBeanFactory}方法，该方法允许以可配置的方式设置父对象。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
//具有父子关系的Bean工厂
public interface HierarchicalBeanFactory extends BeanFactory {

	/**
	 * 返回父bean工厂，如果没有则返回{@code null}。
	 */
	//获取父级工厂
	@Nullable
	BeanFactory getParentBeanFactory();

	/**
	 * 返回当前bean工厂是否包含给定名称的bean，忽略在父级上下文中定义的bean。
	 * <p>这是{@code containsBean}的替代方案，只不过本方法会忽略来自祖先bean工厂的给定名称的bean
	 * @param name 要查询的bean的名称
	 * @return 是否在本地工厂中定义具有给定名称的bean
	 * @see BeanFactory#containsBean
	 */
	//是否包含这个Bean
	boolean containsLocalBean(String name);

}
