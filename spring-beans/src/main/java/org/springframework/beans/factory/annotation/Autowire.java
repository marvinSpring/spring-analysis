/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * 确定自动装配状态的枚举:
 * 也就是说，一个bean是否应该使用setter注入由Spring容器自动注入其依赖项。
 * 这是Spring DI中的核心概念。
 *
 * <p>可用于基于注解的配置，例如用于AspectJ AnnotationBeanConfigurer方面。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.annotation.Configurable
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory
 */
public enum Autowire {

	/**
	 * 常量，表示根本没有自动装配。
	 */
	NO(AutowireCapableBeanFactory.AUTOWIRE_NO),

	/**
	 * 常量，指示按名称自动装配bean属性。
	 */
	BY_NAME(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME),

	/**
	 * 常量，指示按类型自动装配bean属性。
	 */
	BY_TYPE(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);


	private final int value;


	Autowire(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	/**
	 * 返回这是否代表一个实际的自动装配值。
	 * @return 是否指定了实际自动装配
	 * (根据name或者类型进行自动装配)
	 */
	public boolean isAutowire() {
		return (this == BY_NAME || this == BY_TYPE);
	}

}
