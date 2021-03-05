/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * Null bean实例的内部表示，
 * 例如，从FactoryBean.getObject()或工厂方法返回的null值。
 * 每个这样的Null bean都由一个专用的NullBean实例表示，
 * 彼此互不相同，从而分别区分从org.springframework.beans.factory.BeanFactory.getBean所有变体返回的每个bean。
 * 但是，每个这样的实例将对#equals(null)返回true并从#toString()返回"null"，
 * 这是可以在外部对其进行测试的方式（因为此类本身不是公共的）。
 *
 * @author Juergen Hoeller
 * @since 5.0
 */
final class NullBean {

	NullBean() {
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return (this == obj || obj == null);
	}

	@Override
	public int hashCode() {
		return NullBean.class.hashCode();
	}

	@Override
	public String toString() {
		return "null";
	}

}
