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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * 属性资源配置程序的抽象基类，用于解析bean定义属性值中的占位符。
 * 将属性文件或其他{@linkplain org.springframework.core.env.PropertySource#source}的实现
 * <em> pull <em>值转换为bean定义。
 *
 * <p>默认的占位符语法遵循Ant Log4J JSP EL样式：
 *
 * <pre class="code">${...}</pre>
 *
 * XML bean定义示例：
 *
 * <pre class="code">
 * &lt;bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"/&gt;
 *   &lt;property name="driverClassName" value="${driver}"/&gt;
 *   &lt;property name="url" value="jdbc:${dbname}"/&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * 示例Properties文件：
 *
 * <pre class="code">driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * 带注释的Bean定义可以使用{@link org.springframework.beans.factory.annotation.Value @Value}注释进行属性替换：
 *
 * <pre class="code">@Value("${person.age}")</pre>
 *
 * 实现检查bean引用中的简单属性值，list，map，props和bean名称。
 * 此外，占位符值还可以交叉引用其他占位符，例如：
 *
 * <pre class="code">rootPath=myrootdirsubPath=${rootPath}/subdir</pre>
 *
 * 与{@link PropertyOverrideConfigurer}相比，此类型的子类允许在bean定义中填充显式占位符。
 *
 * <p>如果配置程序无法解析占位符，则将引发{@link BeanDefinitionStoreException}异常。
 * 如果要检查多个属性文件，请通过{@link #setLocations locations}属性指定多个资源。
 * 您还可以定义多个配置程序，每个配置程序都有自己的占位符语法。
 * 如果无法解析占位符，请使用{@link #ignoreUnresolvablePlaceholders}有意抑制抛出异常。
 *
 * <p>可以通过{@link #setProperties properties}属性为每个配置程序实例全局定义默认属性值，
 * 也可以使用默认值分隔符（默认情况下为{@code“：”}）并通过{@link #setValueSeparator（String）}设置全局默认属性值。
 *
 * <p>具有默认值的示例XML属性：
 *
 * <pre class="code">
 *   <property name="url" value="jdbc:${dbname:defaultdb}"/>
 * </pre>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertyPlaceholderConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer
		implements BeanNameAware, BeanFactoryAware {

	/** Default placeholder prefix: {@value}. */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: {@value}. */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/** Default value separator: {@value}. */
	public static final String DEFAULT_VALUE_SEPARATOR = ":";


	/** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX}. */
	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX}. */
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	/** Defaults to {@value #DEFAULT_VALUE_SEPARATOR}. */
	@Nullable
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean trimValues = false;

	@Nullable
	protected String nullValue;

	protected boolean ignoreUnresolvablePlaceholders = false;

	@Nullable
	private String beanName;

	@Nullable
	private BeanFactory beanFactory;


	/**
	 * Set the prefix that a placeholder string starts with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_PREFIX}.
	 */
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that a placeholder string ends with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
	 */
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholder variable
	 * and the associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
	 */
	public void setValueSeparator(@Nullable String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Specify whether to trim resolved values before applying them,
	 * removing superfluous whitespace from the beginning and end.
	 * <p>Default is {@code false}.
	 * @since 4.3
	 */
	public void setTrimValues(boolean trimValues) {
		this.trimValues = trimValues;
	}

	/**
	 * Set a value that should be treated as {@code null} when resolved
	 * as a placeholder value: e.g. "" (empty String) or "null".
	 * <p>Note that this will only apply to full property values,
	 * not to parts of concatenated values.
	 * <p>By default, no such null value is defined. This means that
	 * there is no way to express {@code null} as a property value
	 * unless you explicitly map a corresponding value here.
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Set whether to ignore unresolvable placeholders.
	 * <p>Default is "false": An exception will be thrown if a placeholder fails
	 * to resolve. Switch this flag to "true" in order to preserve the placeholder
	 * String as-is in such a case, leaving it up to other placeholder configurers
	 * to resolve it.
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {

		//使用指定的字符串解析器valueResolver 定义一个bean定义信息访问器
		//该访问器的目的就是每次访问一个bean定义信息，将其bean中的可能包含占位符的属性值，包括类的属性
		//bean构造函数的参数、双亲bean的名称、bean的类名称、bean工厂的bean名称、bean工厂的方法名称、作用域等
		//属性全部循环遍历，找到需要解析的占位符对其进行必要的解析
		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		//获取bean工厂中所有的bean名称
		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		//遍历bean定义信息对其进行访问，看其是否需要被解析
		for (String curName : beanNames) {
			// Check that we're not parsing our own bean definition,
			// to avoid failing on unresolvable placeholders in properties file locations.
			//检查当前bean名称，必须不能是已经处理的bean名称，并且处理的容器是自己所在的容器
			if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
				try {
					//对bean定义信息的属性进行占位符解析替换
					visitor.visitBeanDefinition(bd);
				}
				catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
				}
			}
		}

		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		//解析别名目标名称和别名中的占位符。
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		//添加 解析嵌入值（如注释属性）中的占位符。
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}

}
