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

package org.springframework.core.convert.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/**
 * 将一个数组转换为另一个数组。
 * 首先将源数组调整为 List，然后委托给 {@link CollectionToArrayConverter} 来执行目标数组转换。
 *
 * @author Keith Donald
 * @author Phillip Webb
 * @since 3.0
 */
final class ArrayToArrayConverter implements ConditionalGenericConverter {

	private final CollectionToArrayConverter helperConverter;

	private final ConversionService conversionService;


	public ArrayToArrayConverter(ConversionService conversionService) {
		this.helperConverter = new CollectionToArrayConverter(conversionService);
		this.conversionService = conversionService;
	}


	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(Object[].class, Object[].class));
	}

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return this.helperConverter.matches(sourceType, targetType);
	}

	@Override
	@Nullable
	public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (this.conversionService instanceof GenericConversionService) {
			TypeDescriptor targetElement = targetType.getElementTypeDescriptor();
			if (targetElement != null &&
					((GenericConversionService) this.conversionService).canBypassConvert(
							sourceType.getElementTypeDescriptor(), targetElement)) {
				return source;
			}
		}
		List<Object> sourceList = Arrays.asList(ObjectUtils.toObjectArray(source));
		return this.helperConverter.convert(sourceList, sourceType, targetType);
	}

}
