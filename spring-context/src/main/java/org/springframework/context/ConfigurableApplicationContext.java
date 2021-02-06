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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.lang.Nullable;

/**
 * SPI接口将由大多数（如果不是全部）应用程序上下文实现。
 * 除了{@link org.springframework.context.ApplicationContext}界面中的应用程序上下文客户端方法之外，
 * 还提供了配置ApplicationContext的功能。
 *
 * <p>此处封装了配置和生命周期方法，
 * 以避免对ApplicationContext客户端代码显而易见。本方法仅应由启动和关闭代码使用。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 03.11.2003
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * Any number of these characters are considered delimiters between
	 * multiple context config paths in a single String value.
	 * @see org.springframework.context.support.AbstractXmlApplicationContext#setConfigLocation
	 * @see org.springframework.web.context.ContextLoader#CONFIG_LOCATION_PARAM
	 * @see org.springframework.web.servlet.FrameworkServlet#setContextConfigLocation
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * Name of the ConversionService bean in the factory.
	 * If none is supplied, default conversion rules apply.
	 * @since 3.0
	 * @see org.springframework.core.convert.ConversionService
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * Name of the LoadTimeWeaver bean in the factory. If such a bean is supplied,
	 * the context will use a temporary ClassLoader for type matching, in order
	 * to allow the LoadTimeWeaver to process all actual bean classes.
	 * @since 2.5
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * Name of the {@link Environment} bean in the factory.
	 * @since 3.1
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 * Name of the System properties bean in the factory.
	 * @see java.lang.System#getProperties()
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * Name of the System environment bean in the factory.
	 * @see java.lang.System#getenv()
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";


	/**
	 * Set the unique id of this application context.
	 * @since 3.0
	 */
	void setId(String id);

	/**
	 * Set the parent of this application context.
	 * <p>Note that the parent shouldn't be changed: It should only be set outside
	 * a constructor if it isn't available when an object of this class is created,
	 * for example in case of WebApplicationContext setup.
	 * @param parent the parent context
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 */
	void setParent(@Nullable ApplicationContext parent);

	/**
	 * Set the {@code Environment} for this application context.
	 * @param environment the new environment
	 * @since 3.1
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * Return the {@code Environment} for this application context in configurable
	 * form, allowing for further customization.
	 * @since 3.1
	 */
	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * 添加一个新的BeanFactoryPostProcessor，在刷新任何bean定义之前，
	 * 将在刷新时将其应用于此应用程序上下文的内部bean工厂。在上下文配置期间调用。
	 * @param postProcessor the factory processor to register
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * 添加一个新的ApplicationListener，它将在上下文事件（例如上下文刷新和上下文关闭）时收到通知。
	 * <p>请注意，如果上下文尚未处于活动状态，则在刷新时将应用此处注册的所有ApplicationListener；
	 * 如果上下文已经处于活动状态，则将与当前事件多播程序一起即时应用。
	 * @param listener the ApplicationListener to register
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 在此应用程序上下文中注册给定的协议解析器，从而允许处理其他资源协议。
	 * <p>任何此类解析程序都将在此上下文的标准解析规则之前调用。
	 * 因此，它也可以覆盖任何默认规则。
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * 加载或刷新配置的持久表示形式，该表示形式可能来自于基于Java的配置，XML文件，
	 * 属性文件，关系数据库模式或其他某种格式。 <p>由于这是一种启动方法，因此，如果失败，
	 * 它应该销毁已创建的单例，以避免悬挂资源。
	 * 换句话说，在调用此方法之后，应实例化所有单例或根本不实例化。
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	void refresh() throws BeansException, IllegalStateException;

	/**
	 * 在JVM运行时中注册一个关闭挂钩，除非当时已关闭该上下文，
	 * 否则在JVM关闭时将其关闭。
	 * <p>可以多次调用此方法。每个上下文实例仅注册一个关闭挂钩（最大数量）。
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	void registerShutdownHook();

	/**
	 * 关闭此应用程序上下文，释放实现可能持有的所有资源和锁。
	 * 这包括销毁所有缓存的单例bean。
	 * <p>注意：<i>不<i>是否在父上下文上调用{@code close}；
	 * 父级上下文具有自己的独立生命周期。
	 * <p>可以多次调用此方法，而不会产生副作用：
	 * 在已关闭的上下文上进行的后续{@code close}调用将被忽略。
	 */
	@Override
	void close();

	/**
	 * Determine whether this application context is active, that is,
	 * whether it has been refreshed at least once and has not been closed yet.
	 * @return whether the context is still active
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();

	/**
	 * 返回此应用程序上下文的内部bean工厂。可用于访问基础工厂的特定功能。
	 * <p>注意：请勿使用此方法对bean工厂进行后处理；单例之前已经实例化。
	 * 使用BeanFactoryPostProcessor来拦截Bean之前的BeanFactory设置过程。
	 * <p>通常，只有在上下文处于活动状态时，即{@link refresh()}和{@link close（）}之间，
	 * 才能访问此内部工厂。 {@link isActive（）}标志可用于检查上下文是否处于适当的状态。
	 * 
	 * @return the underlying bean factory
	 * @throws IllegalStateException if the context does not hold an internal
	 * bean factory (usually if {@link #refresh()} hasn't been called yet or
	 * if {@link #close()} has already been called)
	 * @see #isActive()
	 * @see #refresh()
	 * @see #close()
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
