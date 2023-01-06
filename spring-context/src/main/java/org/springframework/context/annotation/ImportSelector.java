/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

/**
 * 本接口是Spring中导入外部配置的核心接口,根据给定的条件(条件一般是一个或者多个的注解),通过该条件判断
 * 要导入哪个配置类。
 *
 * 如果实现本接口的同时还实现了一些Aware接口,那么这些Aware能力方法 的生命周期 将在
 * 本接口能力 selectImports方法 之前被陆续调用 {@link ConfigurationClassParser#processImports} into {@see if (candidate.isAssignable(ImportSelector.class)) }'s contents
 *
 * 如果实现本接口的能力类 需要再所有的@Configuration配置类都处理完成后再导入 , 可以实现 {@link DeferredImportSelector}
 *
 *
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 *
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>{@code ImportSelector} implementations are usually processed in the same way
 * as regular {@code @Import} annotations, however, it is also possible to defer
 * selection of imports until all {@code @Configuration} classes have been processed
 * (see {@link DeferredImportSelector} for details).
 *
 * @author Chris Beams
 * @since 3.1
 * @see DeferredImportSelector
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
public interface ImportSelector {

	/**
	 * 根据导入 @{@link 配置} 类的 {@link 注释元数据} 选择并返回应导入的类的名称
	 * @return 要导入的 配置类 类名，如果没有，则为空数组
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

}
