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

package org.springframework.core.convert.converter;

/**
 *
 * converter转换器的工厂类，用来获取对应的转换器
 *
 * “范围”转换器的工厂，可以将对象从 S 转换为 R 的子类型。
 *
 * 可以另外实现{@link ConditionalConverter}。
 *
 * @author Keith Donald
 * @since 3.0
 * @param <S> the source type converters created by this factory can convert from
 * @param <R> the target range (or base) type converters created by this factory can convert to;
 * for example {@link Number} for a set of number subtypes.
 * @see ConditionalConverter
 */
public interface ConverterFactory<S, R> {

	/**
	 * 获取转换器从 S 转换为目标类型 T，其中 T 也是 R 的实例。
	 * @param <T>  目标类型
	 * @param targetType  要转换为的目标类型
	 * @return 从 S 到 T 的转换器
	 */
	<T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
