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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 提供配置框架和基本功能
 *
 *
 * spring的根接口
 * This is the basic client view of a bean container;
 * 还有其他接口，例如{@link ListableBeanFactory}
 * 和{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}可发挥特殊作用。
 *
 * <p>该接口由包含多个bean定义的对象实现，每个bean定义均都有一个唯一的字符串名称标识。
 * 根据bean的定义，工厂将返回包含对象(单例)的（Prototype设计模式）bean，
 * 或者返回一个多例（Singleton设计模式的替代方案，其中实例是作用域中的单例）的工厂）。
 * 返回哪种类型的实例取决于bean工厂的配置：API是相同的。
 *
 * <p>实现上面方法的重点是BeanFactory是应用程序组件的中央注册表，
 * 并且集中了ApplicationComponent的配置（例如，不再需要单个对象读取属性文件）。
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 *
 * <p>通常，BeanFactory将加载存储在配置源（例如XML文档）中的bean定义，
 * 并使用{@code org.springframework.beans}包来配置bean。
 * 但是，实现类可以根据需要直接在Java代码中直接返回它创建的Java对象
 * 定义的存储方式没有任何限制：LDAP，RDBMS，XML，属性文件等。
 *
 * 鼓励实现支持Bean之间的引用（依赖注入）。
 *
 * {@link ListableBeanFactory}让bean的定义是可罗列的，有列表形式的
 * {@link HierarchicalBeanFactory}提供父子容器关系的容器
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * <p>Bean工厂实现应尽可能支持标准Bean生命周期接口。完整的初始化方法及其标准顺序为：
 * <ol>
	 * <li>BeanNameAware's {@code setBeanName}
	 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
	 * <li>BeanFactoryAware's {@code setBeanFactory}
	 * <li>EnvironmentAware's {@code setEnvironment}
	 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
	 * <li>ResourceLoaderAware's {@code setResourceLoader}
	 * (only applicable when running in an application context)
	 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
	 * (only applicable when running in an application context)
	 * <li>MessageSourceAware's {@code setMessageSource}
	 * (only applicable when running in an application context)
	 * <li>ApplicationContextAware's {@code setApplicationContext}
	 * (only applicable when running in an application context)
	 * <li>ServletContextAware's {@code setServletContext}
	 * (only applicable when running in a web application context)
	 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
	 * <li>InitializingBean's {@code afterPropertiesSet}
	 * <li>a custom init-method definition
	 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 *
 * <p>在关闭bean工厂时，以下生命周期方法适用：
 * <ol>
	 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
	 * <li>DisposableBean's {@code destroy}
	 * <li>a custom destroy-method definition
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 用于接触引用{@link FactoryBean}实例，并将其与由FactoryBean创建的Bean 区别开来。
	 例如，如果名为{@code myJndiObject}的bean是FactoryBean，
	 则获取{@code ＆myJndiObject}将返回工厂，而不是工厂生产的实例。
	 */
	//在getBean的时候加这个取地址符会将该bean的工厂实例获取到，而不是获取到工厂生产的bean对象
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回一个实例，该实例可以是指定bean的共享或独立的。
	 * <p>此方法允许使用Spring BeanFactory替代Singleton或Prototype设计模式。
	 * 对于Singleton bean，调用者可以保留对返回对象的引用。
	 * <p>将别名转换回相应的规范bean名称。将询问父工厂是否在该工厂实例中找不到该bean。
	 * @param name 要检索的bean的名称
	 * @return Bean的一个实例
	 * @throws NoSuchBeanDefinitionException 如果没有指定名称的bean
	 * @throws BeansException 如果无法获取Bean
	 */
	//下面几个getBean差不多的
	Object getBean(String name) throws BeansException;

	/**
	 * 返回指定 bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * 本方法的行为与{@link #getBean(String)}相同，但提供了一个类型参数
	 * 通过抛出BeanNotOfRequiredTypeException(如果bean不是
	 * 所需的类型。这意味着不能在强制转换时抛出ClassCastException
	 * 结果正确，就像使用{@link #getBean(String)}时一样。
	 * 将别名转换回相应的规范bean名称。
	 * 将询问父工厂是否不能在这个工厂实例中找到bean。
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	//根据bean的名称和类型限定获取bean实例
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
	 * 允许指定显式的构造函数参数/工厂方法参数，
	 * 在bean定义中重写指定的默认参数(如果有的话)。
	 * @param name the name of the bean to retrieve
	 * @param args 使用显式参数创建Bean实例时要使用的参数（仅在创建新实例而不是检索现有实例时才应用）
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	//根据bean的名称和指定构造方法获取bean实例
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 返回唯一匹配给定对象类型的bean实例(如果有的话)。
	 * 这个方法进入到{@link ListableBeanFactory}的类型查找区域
	 * 但也可能被转换为基于名字的常规姓名查找
	 * 给定类型的。对于跨bean集的更广泛的检索操作，
	 * 使用{@link ListableBeanFactory}和/或{@link BeanFactoryUtils}。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	//获取bean的提供者
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * Does this bean factory contain a bean definition or externally registered singleton
	 * instance with the given name?
	 * <p>If the given name is an alias, it will be translated back to the corresponding
	 * canonical bean name.
	 * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
	 * be found in this factory instance.
	 * <p>If a bean definition or singleton instance matching the given name is found,
	 * this method will return {@code true} whether the named bean definition is concrete
	 * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
	 * return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	//判断Spring容器中是否有bean名称为name的这个bean
	boolean containsBean(String name);

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean} always
	 * return the same instance?
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * independent instances. It indicates non-singleton instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
	 * check for independent instances.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	//判断该bean是否是单例
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return
	 * independent instances?
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * a singleton object. It indicates non-independent instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
	 * check for a shared singleton instance.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	//判断该bean是否是多例
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 检查具有给定名称的Bean是否与指定的类型匹配。
	 * 更具体地说，检查对给定名称的{@link #getBean}调用是否会返回可分配给指定目标类型的对象。
	 * <p>将别名转换回相应的规范bean名称。将询问父工厂是否在该工厂实例中找不到该bean。
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 检查具有给定名称的Bean是否与指定的类型匹配。
	 * 更具体地说，检查对给定名称的{@link #getBean}调用是否会返回可分配给指定目标类型的对象。
	 * <p>将别名转换回相应的规范bean名称。
	 * 将询问父工厂是否在该工厂实例中找不到该 bean。
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 确定具有给定名称的bean的类型。更具体地说，
	 * 确定{@link #getBean}将针对给定名称返回的对象类型。
	 * <p>对于{@link FactoryBean}，返回由{@link FactoryBean#getObjectType}公开的FactoryBean创建的对象的类型。
	 * <p>将别名转换回相应的规范bean名称。将询问父工厂是否在该工厂实例中找不到该bean。
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 *返回给定bean名称的别名（如果有）。在{@link #getBean}调用中使用时，所有这些别名都指向同一个bean。
	 * <p>如果给定名称是别名，则将返回相应的原始bean名称和其他别名（如果有），原始bean名称是数组中的第一个元素。
	 * <p>将询问父工厂是否在该工厂实例中找不到该bean。
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
