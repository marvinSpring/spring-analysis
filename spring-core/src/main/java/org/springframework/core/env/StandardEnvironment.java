/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.core.env;

/**
 *适用于“标准的”（即非网络读取的）Application的{@link Environment}实现。
 *
 * <p>除了{@link ConfigurableEnvironment}的常用功能（例如属性解析和与配置文件相关的操作）之外，
 * 此实现还配置了两个默认属性源，将按以下顺序搜索：
 * <ul>
	 * <li>{@linkplain AbstractEnvironment#getSystemProperties() 系统属性}
	 * <li>{@linkplain AbstractEnvironment#getSystemEnvironment() 系统环境变量}
 * </ul>
 *
 * 也就是说，如果键“ xyz”同时存在于JVM系统属性以及当前进程的环境变量集中，
 * 则系统属性中键“ xyz”的值将从对{@code environment.getProperty("xyz")}.
 * 默认情况下选择此顺序，因为系统属性是针对每个JVM的，而环境变量在给定系统上的许多JVM中可能是相同的。
 * 通过赋予系统属性优先级，可以基于每个JVM覆盖环境变量。
 *
 * <p>这些默认属性源可能会被删除，重新排序或替换。
 * 并且可以使用{@link #getPropertySources()}中的{@link MutablePropertySources}实例添加其他属性源。
 * 有关使用示例，请参见{@link ConfigurableEnvironment} Javadoc。
 *
 * <p>请参阅{@link SystemEnvironmentPropertySource} Javadoc，
 * 以获取有关操作系统环境（例如Bash）中属性名称的特殊处理的详细信息，这些属性不允许变量名称中使用句点字符。
 *
 * @author Chris Beams
 * @since 3.1
 * @see ConfigurableEnvironment
 * @see SystemEnvironmentPropertySource
 * @see org.springframework.web.context.support.StandardServletEnvironment
 */
public class StandardEnvironment extends AbstractEnvironment {

	/** 系统环境属性源名称： {@value}. */
	public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";

	/** JVM系统属性属性源名称： {@value}. */
	public static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";


	/**
	 * 使用适用于任何标准的资源来自定义资源集
	 * Java environment:
	 * <ul>
	 * <li>{@value #SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME}
	 * <li>{@value #SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}
	 * </ul>
	 * <p>{@value SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME}中存在的属性将优先于{@value SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME}中的属性。
	 * @see AbstractEnvironment#customizePropertySources(MutablePropertySources)
	 * @see #getSystemProperties()
	 * @see #getSystemEnvironment()
	 *///就是说操作系统属性的优先级高于JVM系统的属性
	@Override
	protected void customizePropertySources(MutablePropertySources propertySources) {
		propertySources.addLast(
				new PropertiesPropertySource(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getSystemProperties()));
		propertySources.addLast(
				new SystemEnvironmentPropertySource(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment()));
	}

}
