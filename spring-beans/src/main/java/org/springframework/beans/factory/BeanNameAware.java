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

package org.springframework.beans.factory;

/**
 * 想要在Bean工厂中知道其Bean名称的Bean将实现的接口。
 * 请注意，通常不建议对象依赖于其bean名称，
 * 因为这表示对外部配置的潜在脆弱依赖，以及对Spring API的不必要依赖。
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 01.11.2003
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanNameAware extends Aware {

	/**
	 * 在创建此bean的bean工厂中设置bean的名称。
	 * <p>在填充正常的bean属性之后但在初始化回调
	 * （例如{@link InitializingBeanafterPropertiesSet（）}或自定义的初始化方法）之前调用。
	 * @param name工厂中的bean的名称。
	 *        请注意，此名称是工厂中使用的实际bean名称，该名称可能与最初指定的名称不同：
	 *        特别是对于内部bean名称，实际的bean名称可能已通过附加“ ...”后缀而变得唯一。
	 *        如果需要，请使用{@link BeanFactoryUtilsoriginalBeanName（String）}方法提取原始bean名称（不带后缀）。
	 */
	void setBeanName(String name);

}
