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

import org.springframework.beans.BeansException;

/**
 * 定义一个工厂，该工厂在调用时可以返回Object实例（可能是共享的或独立的）。
 *
 * <p>此接口通常用于封装通用工厂，该通用工厂在每次调用时返回某个目标对象的新实例（prototype）。
 *
 * <p>该接口与{@link FactoryBean}类似，但是后者的实现通常是在{@link BeanFactory}中定义为SPI实例，
 * 而此类的实现通常是作为API馈送到其他bean（通过注射）。
 * 因此，{@code #getObject()}方法具有不同的异常处理行为。
 *
 * @author Colin Sampaleanu
 * @since 1.0.2
 * @param <T> the object type
 * @see FactoryBean
 */
@FunctionalInterface
public interface ObjectFactory<T> {

	/**
	 * 返回此工厂管理的对象的实例（可能是共享的或独立的）。
	 * @return the resulting instance
	 * @throws BeansException in case of creation errors
	 */
	T getObject() throws BeansException;

}
