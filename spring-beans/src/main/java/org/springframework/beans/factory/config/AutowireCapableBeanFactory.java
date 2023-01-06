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

package org.springframework.beans.factory.config;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.beans.factory.BeanFactory}接口的扩展将由能够自动装配的bean工厂实现，
 * 前提是它们希望为现有的bean实例公开此功能。
 *
 * <p>BeanFactory的此子接口不能在常规应用程序代码中使用：
 * 在典型的使用情况下，请坚持使用{@link org.springframework.beans.factory.BeanFactory}
 * 或{@link org.springframework.beans.factory.ListableBeanFactory}。
 *
 * <p>其他框架的集成代码可以利用此接口来连接和填充Spring
 * 无法控制其生命周期的现有bean实例。
 * 例如，这对于WebWork操作和Tapestry页面对象特别有用。
 *
 * <p>请注意，{@link org.springframework.context.ApplicationContext}外观未实现此接口，
 * 因为应用程序代码几乎从未使用过此接口。也就是说，它也可以从应用程序上下文中获得，
 * 可以通过ApplicationContext的{@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}方法进行访问。
 *
 * <p>您还可以实现{@link org.springframework.beans.factory.BeanFactoryAware}接口，
 * 该接口即使在ApplicationContext中运行时也公开内部BeanFactory，以访问AutowireCapableBeanFactory：
 * 只需将传入的BeanFactory强制转换为AutowireCapableBeanFactory。
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * 常量，指示没有外部定义的自动装配。
	 * 注意，BeanFactoryAware等和注释驱动的注入仍将被应用。
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * 常量，指示按名称自动装配bean属性
	 * (应用于所有bean属性设置器)。
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * 常量，指示按类型自动装配bean属性
	 * (应用于所有bean属性设置器)。
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * 常量，指示自动装配可以满足的最多参数的构造函数
	 * (包括解析适当的构造函数).
	 * @see #createBean
	 * @see #autowire
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * 常量，
	 * 指示通过bean类的自省确定适当的自动装配策略。
	 * @see #createBean
	 * @see #autowire
	 * @deprecated 从Spring 3.0开始:如果您正在使用混合自动装配策略，
	 * 请选择基于注解的自动装配，以便更清楚地划分自动装配需求。
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;

	/**
	 * 初始化现有bean实例时“原始实例”约定的后缀:
	 * 将被追加到完全限定bean类名后，
	 * 例如。“com.mypackage.MyClass.ORIGINAL”，
	 * 以强制返回给定的实例，即没有代理等。
	 * @since 5.1
	 * @see #initializeBean(Object, String)
	 * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
	 * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
	 */
	String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";


	//-------------------------------------------------------------------------
	// 用于创建和填充外部bean实例的典型方法
	//-------------------------------------------------------------------------

	/**
	 * 完全创建给定类的一个新的bean实例。
	 * <p>执行bean的完全初始化，
	 * 包括所有适用的{@link BeanPostProcessor BeanPostProcessors}。
	 * <p>注意:
	 * 这是为了创建一个新实例，填充带注解的字段和方法，以及应用所有标准bean初始化回调。
	 * <i>而不是<i>它是否暗示传统的按名称或按类型自动装配属性;
	 * 使用 {@link #createBean(Class, int, boolean)} 用于上面描述的目的.
	 * @param beanClass 要创建的bean的类
	 * @return 新的bean实例
	 * @throws BeansException 如果实例化或连接失败
	 */
	//创建一个bean对象
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * 通过应用实例化后回调和bean属性后处理
	 * (例如注解驱动的注入)来填充给定的bean实例。
	 * 注意:
	 * 这本质上是为了(重新)填充带注解的字段和方法，无论是为新实例还是反序列化实例。
	 * <i>而不是<i>它是否暗示传统的按名称或按类型自动装配属性;
	 * 使用{@link #autowireBeanProperties}来实现这些目的。
	 * @param existingBean 现有的bean实例
	 * @throws BeansException 如果填充失败
	 */
	//对给定已经存在的bean实例进行自动注入
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * 配置给定的原始bean:
	 * 自动装配bean属性、应用bean属性值、应用工厂回调
	 * (如{@code setBeanName}和{@code setBeanFactory})，
	 * 以及应用所有bean后处理器(包括可能包装给定原始bean的处理器)。
	 * 这实际上是{@link #initializeBean}提供的一个super-interface，
	 * 完全应用了相应bean定义指定的配置。
	 * <b>注意:该方法需要一个给定名称的bean定义!</b>
	 * @param existingBean 现有的bean实例
	 * @param beanName bean的名称，必要时传递给它
	 *                    (该名称的bean定义必须可用)
	 * @return 要使用的bean实例，无论是原始的还是封装的
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * 如果没有给定名称的bean定义
	 * @throws BeansException 如果初始化失败
	 * @see #initializeBean
	 */
	//对给定的原始bean进行配置,比如自动装配其属性,应用属性值到bean实例,bean工厂回调方法
	Object configureBean(Object existingBean, String beanName) throws BeansException;


	//-------------------------------------------------------------------------
	//用于对bean生命周期进行细粒度控制的专门方法
	//-------------------------------------------------------------------------

	/**
	 * 使用指定的自动装配策略完全创建给定类的新bean实例。
	 * 这里支持此接口中定义的所有常量。
	 * <p>执行bean的完全初始化，包括所有适用的{@link BeanPostProcessor BeanPostProcessors}。
	 * 这实际上是{@link #autowire}提供的一个super-interface，
	 * 添加了{@link #initializeBean}行为。
	 * @param beanClass 要创建的bean的类
	 * @param autowireMode 通过名称或类型，使用此接口中的常量
	 * @param dependencyCheck 是否对对象进行依赖项检查
	 * (不适用于自动装配构造函数，因此构造函数自动装配将可以忽略该参数)
	 * @return 创建好的bean实例
	 * @throws BeansException 如果实例化或者注入失败
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 */
	//创建bean,以及指定创建的时候适用的自动注入模式与是否进行依赖检查
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * 用指定的自动装配策略去实例化给定类的一个新的bean对象。
	 * 这里支持此接口中定义的所有常量。
	 * 也可以使用{@code AUTOWIRE_NO}调用，以便在实例化前应用回调
	 * (例如用于注解驱动的注入)。
	 * <p><i>不适用<i>标准的{@link BeanPostProcessor BeanPostProcessors}回调或执行bean的任何进一步初始化。
	 * 该接口为这些目的提供了不同的细粒度操作，
	 * 例如{@link #initializeBean}。
	 * 但是，如果适用于实例的构造，
	 * 将应用{@link InstantiationAwareBeanPostProcessor}回调。
	 * @param beanClass 要实例化的bean的类
	 * @param autowireMode 使用此接口中的常量标识是要通过名称或类型进行注入
	 * @param dependencyCheck 是否对bean实例中的对象引用执行依赖项检查
	 * (自动装配构造函数方式,将忽略本参数)
	 * @return 一个新的bean实例
	 * @throws BeansException 如果实例化或者注入异常
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #initializeBean
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	//给bean注入属性
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * 根据名称或类型自动装配给定bean实例的bean属性。
	 * 也可以使用{@code AUTOWIRE_NO}调用，
	 * 以便应用实例化后的回调(例如用于注释驱动的注入)。
	 * <p>不适用标准的{@link BeanPostProcessor BeanPostProcessors}回调，
	 * 也不执行bean的任何进一步初始化。
	 * 该接口为这些目的提供了不同的细粒度操作，
	 * 例如{@link #initializeBean}。
	 * 但是，如果适用于实例的配置，
	 * 将应用{@link InstantiationAwareBeanPostProcessor}回调。
	 * @param existingBean 已存在的bean实例
	 * @param autowireMode 使用此接口中的常量表示要通过名称或类型自动注入
	 * @param dependencyCheck 是否对bean实例中的对象引用执行依赖项检查
	 * @throws BeansException 如果注入失败
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_NO
	 */
	//给已经存在的bean实例去注入属性
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 * 将具有给定名称的 bean定义 的 属性值 应用到给定的 bean实例。
	 * bean定义既可以定义一个完全自包含的bean，
	 * 重用它的属性值，也可以只定义用于现有bean实例的属性值。
	 * <p>此方法不自动装配bean属性;
	 * 它只是应用显式定义的属性值。使用{@link #autowireBeanProperties}方法自动装配一个现有的bean实例。
	 * <b>注意:该方法需要一个给定名称的bean定义!</b>
	 * <p>不应用标准的{@link BeanPostProcessor BeanPostProcessors}回调，
	 * 也不执行bean的任何进一步初始化。
	 * 该接口为这些目的提供了不同的细粒度操作，
	 * 例如{@link #initializeBean}。
	 * 但是，如果适用于实例的配置，
	 * 将应用{@link InstantiationAwareBeanPostProcessor}回调。
	 * @param existingBean 已存在的bean实例
	 * @param beanName bean工厂中bean定义的名称
	 * (该名称的bean定义必须可用,也就是beanDefinitionNames集合中存在的bean名称)
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * 如果没有给定名称的bean定义
	 * @throws BeansException 如果应用属性值失败
	 * @see #autowireBeanProperties
	 */
	//将bean的属性值应用到bean实例对象中
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * 初始化给定的原始bean，
	 * 应用工厂回调，
	 * 例如{@code setBeanName}和{@code setBeanFactory}，
	 * 还应用所有bean后置处理器(包括可能包装给定原始bean的处理器)。
	 * <p>注意，
	 * 给定名称的bean定义不必存在于bean工厂中。
	 * 传入的bean名称将仅用于回调，而不会检查已注册的bean定义。
	 * @param existingBean 已存在的bean实例
	 * @param beanName bean的名称，必要时传递给它
	 * (只传递给{@link BeanPostProcessor BeanPostProcessors};
	 * 可以遵循{@link #ORIGINAL_INSTANCE_SUFFIX}约定，以强制返回给定的实例，即没有代理等)
	 * @return 要使用的bean实例，无论是原始的还是封装的
	 * @throws BeansException 如果实例化该bean失败
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 */
	//原始bean的init-method方法
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * 将{@link BeanPostProcessor BeanPostProcessors}应用到给定的现有bean实例，
	 * 调用它们的{@code postProcessBeforeInitialization}方法。
	 * 返回的bean实例可能是原始bean实例的包装器。
	 * @param existingBean 已存在的bean实例
	 * @param beanName 在必要时传递到本方法bean的名称
	 * (只传递给{@link BeanPostProcessor BeanPostProcessors};
	 * 可以遵循{@link #ORIGINAL_INSTANCE_SUFFIX}约定，以强制返回给定的实例，即没有代理等)
	 * @return 要使用的bean实例，无论是原始的还是封装的
	 * @throws BeansException 如果任何一个前置处理器执行失败
	 * @see BeanPostProcessor#postProcessBeforeInitialization
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 */
	//将所有BPP的postProcessBeforeInitialization能力应用到给定的bean实例,     它将把前置处理器能力赋能给当前bean
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * 将{@link BeanPostProcessor BeanPostProcessors}应用到给定的现有bean实例，
	 * 调用它们的{@code postProcessAfterInitialization}方法。
	 * 返回的bean实例可能是原始bean实例的包装器
	 * @param existingBean 已存在的bean实例
	 * @param beanName bean的名称，在必要时传递给它
	 * (只传递给{@link BeanPostProcessor BeanPostProcessors};
	 * 可以遵循{@link #ORIGINAL_INSTANCE_SUFFIX}约定，以强制返回给定的实例，即没有代理等)
	 * @return 在bean被实例化的时候适用, 原始bean或者包装bean
	 * @throws BeansException 如果任何bean后置处理器执行失败
	 * @see BeanPostProcessor#postProcessAfterInitialization
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 */
	//将所有BPP的postProcessAfterInitialization能力应用到给定的bean实例,     它将把后置处理器能力赋能给当前bean
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * 销毁给定的bean实例(通常来自{@link #createBean})，
	 * 应用{@link org.springframework.beans.factory.DisposableBean}。
	 * 联系并注册{@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}。
	 * <p>销毁过程中出现的任何异常都应该被捕获并记录，
	 * 而不是传播给该方法的调用者。
	 * @param existingBean 要被销毁的bean实例
	 */
	//bean销毁方法
	void destroyBean(Object existingBean);


	//-------------------------------------------------------------------------
	// 委托下面这些方法们去解析注入点
	//-------------------------------------------------------------------------

	/**
	 * 解析唯一匹配给定对象类型(如果有的话)的bean实例，
	 * 包括它的bean名称。
	 * <p>这实际上是{@link #getBean(Class)}的一个变体，
	 * 它保留了匹配实例的bean名。
	 * @param requiredType bean必须匹配的类型;可以是接口或者父类
	 * @return bean名称和bean实例的一个holder
	 * @throws NoSuchBeanDefinitionException 如果没有匹配到对应的bean
	 * @throws NoUniqueBeanDefinitionException 如果根据类型匹配到多个bean
	 * @throws BeansException 如果这个bean不能去被创建
	 * @since 4.3.3
	 * @see #getBean(Class)
	 */
	//类似于getBean
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	/**
	 * Resolve a bean instance for the given bean name, providing a dependency descriptor
	 * for exposure to target factory methods.
	 * <p>This is effectively a variant of {@link #getBean(String, Class)} which supports
	 * factory methods with an {@link org.springframework.beans.factory.InjectionPoint}
	 * argument.
	 * @param name the name of the bean to look up
	 * @param descriptor the dependency descriptor for the requesting injection point
	 * @return the corresponding bean instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be created
	 * @since 5.1.5
	 * @see #getBean(String, Class)
	 */
	Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

	/**
	 * 针对此工厂中定义的bean解析指定的依赖项。
	 * @param descriptor 依赖项的描述符 (属性/方法/构造器)
	 * @param requestingBeanName 声明给定依赖项的bean的名称
	 * @return 解析对象，如果没有找到则为{@code null}
	 * @throws NoSuchBeanDefinitionException 如果没有找到bean定义信息
	 * @throws NoUniqueBeanDefinitionException 如果找到俩个以上的bean定义信息
	 * @throws BeansException 如果依赖项解析因任何其他原因失败
	 * @since 2.5
	 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
	 */
	//对指定的bean名称解析其对应的依赖描述
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * @param descriptor the descriptor for the dependency (field/method/constructor)
	 * @param requestingBeanName the name of the bean which declares the given dependency
	 * @param autowiredBeanNames a Set that all names of autowired beans (used for
	 * resolving the given dependency) are supposed to be added to
	 * @param typeConverter the TypeConverter to use for populating arrays and collections
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see DependencyDescriptor
	 */
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
