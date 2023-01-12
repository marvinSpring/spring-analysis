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

package org.springframework.context.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.*;

/**
 *{@link org.springframework.context.ApplicationContext}接口的抽象实现。
 * 不强制用于配置的存储类型；简单地实现通用上下文功能。
 * 使用模板方法设计模式，需要具体的子类来实现抽象方法。
 *
 * <p>In contrast to a plain BeanFactory, an ApplicationContext is supposed
 * to detect special beans defined in its internal bean factory:
 * Therefore, this class automatically registers
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors},
 * and {@link org.springframework.context.ApplicationListener ApplicationListeners}
 * which are defined as beans in the context.
 *
 * <p>A {@link org.springframework.context.MessageSource} may also be supplied
 * as a bean in the context, with the name "messageSource"; otherwise, message
 * resolution is delegated to the parent context. Furthermore, a multicaster
 * for application events can be supplied as an "applicationEventMulticaster" bean
 * of type {@link org.springframework.context.event.ApplicationEventMulticaster}
 * in the context; otherwise, a default multicaster of type
 * {@link org.springframework.context.event.SimpleApplicationEventMulticaster} will be used.
 *
 * <p>Implements resource loading by extending
 * {@link org.springframework.core.io.DefaultResourceLoader}.
 * Consequently treats non-URL resource paths as class path resources
 * (supporting full class path resource names that include the package path,
 * e.g. "mypackage/myresource.dat"), unless the {@link #getResourceByPath}
 * method is overridden in a subclass.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @since January 21, 2001
 * @see #refreshBeanFactory
 * @see #getBeanFactory
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.MessageSource
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext {

	/**
	 * Name of the MessageSource bean in the factory.
	 * If none is supplied, message resolution is delegated to the parent.
	 * @see MessageSource
	 */
	public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

	/**
	 * Name of the LifecycleProcessor bean in the factory.
	 * If none is supplied, a DefaultLifecycleProcessor is used.
	 * @see org.springframework.context.LifecycleProcessor
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

	/**
	 * Name of the ApplicationEventMulticaster bean in the factory.
	 * If none is supplied, a default SimpleApplicationEventMulticaster is used.
	 * @see org.springframework.context.event.ApplicationEventMulticaster
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";


	static {
		// Eagerly load the ContextClosedEvent class to avoid weird classloader issues
		// on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
		//该方法是为了解决WebLogic 8.1的Bug 可忽略
		ContextClosedEvent.class.getName();
	}


	//创建日志,子类会用到这个日志处理器
	protected final Log logger = LogFactory.getLog(getClass());

	//生成spring容器当前的id,容器对象的内存地址的hash值
	private String id = ObjectUtils.identityToString(this);

	/** Display name. */
	//指定spring上下文的名称
	private String displayName = ObjectUtils.identityToString(this);

	/** 当前上下文的父级上下文. */
	@Nullable
	private ApplicationContext parent;

	/** Environment used by this context. */
	//spring上下文的环境配置
	@Nullable
	private ConfigurableEnvironment environment;

	//存储BeanFactory的前置处理器
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

	//当前上下文启动的时间戳
	private long startupDate;

	//当前上下文当前是否处于激活状态的状态位
	private final AtomicBoolean active = new AtomicBoolean();

	//当前上下文当前是否处于已经被关闭的状态位   它与active属性具有互斥性
	private final AtomicBoolean closed = new AtomicBoolean();

	//初始化一个同步监听器（该监听器用来监听Spring上下文的刷新和销毁）
	private final Object startupShutdownMonitor = new Object();

	/** Reference to the JVM shutdown hook, if registered. */
	@Nullable
	private Thread shutdownHook;

	//当前上下文中使用的ResourcePatternResolver
	private final ResourcePatternResolver resourcePatternResolver;

	/** LifecycleProcessor for managing the lifecycle of beans within this context. */
	@Nullable
	private LifecycleProcessor lifecycleProcessor;

	/** 消息源. */
	@Nullable
	private MessageSource messageSource;

	/** Helper class used in event publishing. */
	//应用程序本地事件多播器
	@Nullable
	private ApplicationEventMulticaster applicationEventMulticaster;

	/** 统计特殊的监听器. */
	private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

	/** 在refresh之前注册的本地监听器. */
	@Nullable
	private Set<ApplicationListener<?>> earlyApplicationListeners;

	/** 在多播器被设置之前完成的事件发布的事件. */
	@Nullable
	private Set<ApplicationEvent> earlyApplicationEvents;


	//创建一个抽象层次的AbstractApplicationContext
	public AbstractApplicationContext() {
		//创建资源模式处理器
		this.resourcePatternResolver = getResourcePatternResolver();
	}

	//使用给定的父上下文创建一个新的AbstractApplicationContext。
	public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		/*
			1.给容器创建一些初始化的对象设置
			2.创建资源模式解析器
			3.如果存在父级容器,则合并本容器和父级容器的环境配置(Environment)
		 */
		this();
		setParent(parent);
	}


	//---------------------------------------------------------------------
	// Implementation of ApplicationContext interface
	//---------------------------------------------------------------------

	/**
	 * Set the unique id of this application context.
	 * <p>Default is the object id of the context instance, or the name
	 * of the context bean if the context is itself defined as a bean.
	 * @param id the unique id of the context
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getApplicationName() {
		return "";
	}

	/**
	 * Set a friendly name for this context.
	 * Typically done during initialization of concrete context implementations.
	 * <p>Default is the object id of the context instance.
	 */
	public void setDisplayName(String displayName) {
		Assert.hasLength(displayName, "Display name must not be empty");
		this.displayName = displayName;
	}

	/**
	 * Return a friendly name for this context.
	 * @return a display name for this context (never {@code null})
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * (that is, this context is the root of the context hierarchy).
	 */
	@Override
	@Nullable
	public ApplicationContext getParent() {
		return this.parent;
	}

	/**
	 * Set the {@code Environment} for this application context.
	 * <p>Default value is determined by {@link #createEnvironment()}. Replacing the
	 * default with this method is one option but configuration through {@link
	 * #getEnvironment()} should also be considered. In either case, such modifications
	 * should be performed <em>before</em> {@link #refresh()}.
	 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * 以可配置的形式返回此应用程序上下文的环境 {@code Environment}，
	 * 允许进一步自定义。
	 * 如果没有自定义的可配置的环境，当前上下文的默认环境将通过{@link #createEnvironment()}方法进行初始化设置。
	 *
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}

	/**
	 * 创建并返回一个新的{@link StandardEnvironment}。
	 * <p>子类可以重写此方法，以提供自定义的可配置形式的环境->{@link ConfigurableEnvironment}实现。
	 */
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardEnvironment();
	}

	/**
	 * 如果此上下文的内部bean工厂已经可用，则将其返回为AutowireCapableBeanFactory。
	 * @see #getBeanFactory()
	 */
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return getBeanFactory();
	}

	/**
	 * 返回首次加载此上下文时的时间戳（毫秒）。
	 */
	@Override
	public long getStartupDate() {
		return this.startupDate;
	}

	/**
	 * 将给定事件发布到所有监听器。
	 * <p>注意：监听器在 MessageSource 之后进行初始化，
	 * 以便能够在侦听器实现中访问它。因此，消息源实现无法发布事件。
	 * @param event 要发布的事件（可能是特定于应用程序的事件，也可能是标准框架事件）
	 */
	@Override
	public void publishEvent(ApplicationEvent event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * <p>Note: Listeners get initialized after the MessageSource, to be able
	 * to access it within listener implementations. Thus, MessageSource
	 * implementations cannot publish events.
	 * @param event the event to publish (may be an {@link ApplicationEvent}
	 * or a payload object to be turned into a {@link PayloadApplicationEvent})
	 */
	@Override
	public void publishEvent(Object event) {
		publishEvent(event, null);
	}

	/**
	 * 将给定事件发布到所有侦听器。
	 * @param event 要发布的事件 (may be an {@link ApplicationEvent}
	 * 或要转换为的有效负载对象 {@link PayloadApplicationEvent})
	 * @param eventType 已解决事件类型（如果已知）
	 * @since 4.2
	 */
	protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");

		// Decorate event as an ApplicationEvent if necessary
		//如有必要，将事件装饰为应用程序事件
		ApplicationEvent applicationEvent;
		//判断传进来的事件是否是一个本地事件,如果是就强转
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			//如果不是本地事件，那么就包装当前事件->本地事件
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
			}
		}

		//-------------
		//如果可能的话，现在进行多播 - 或者在初始化组播器后懒惰地进行多播
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			//获取Spring当前上下文中的多播器，并将当前事件类型发布到多播器中所有的监听器中
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// 也会通过父上下文发布事件...
		if (this.parent != null) {
			//如果当前Spring上下文有父级上下文，并且是抽象层次的父级上下文，那么就用父级的抽象上下文进行发布事件
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				//如果有父级上下文，但不是抽象层次的父级上下文，那么就直接发布
				this.parent.publishEvent(event);
			}
		}
	}

	/**
	 * Return the internal ApplicationEventMulticaster used by the context.
	 * @return the internal ApplicationEventMulticaster (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (this.applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
					"call 'refresh' before multicasting events via the context: " + this);
		}
		return this.applicationEventMulticaster;
	}

	/**
	 * Return the internal LifecycleProcessor used by the context.
	 * @return the internal LifecycleProcessor (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
		if (this.lifecycleProcessor == null) {
			throw new IllegalStateException("LifecycleProcessor not initialized - " +
					"call 'refresh' before invoking lifecycle methods via the context: " + this);
		}
		return this.lifecycleProcessor;
	}

	/**
	 * Return the ResourcePatternResolver to use for resolving location patterns
	 * into Resource instances. Default is a
	 * {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},
	 * supporting Ant-style location patterns.
	 * <p>Can be overridden in subclasses, for extended resolution strategies,
	 * for example in a web environment.
	 * <p><b>Do not call this when needing to resolve a location pattern.</b>
	 * Call the context's {@code getResources} method instead, which
	 * will delegate to the ResourcePatternResolver.
	 * @return the ResourcePatternResolver for this context
	 * @see #getResources
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	// 返回用于将路径解析为资源实例的ResourceLoader。
	// 默认是一个 PathMatchingResourcePatternResolver
	// 支持ant表达式的路径。
	protected ResourcePatternResolver getResourcePatternResolver() {
		//用来解析xml文件
		return new PathMatchingResourcePatternResolver(this);
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurableApplicationContext interface
	//---------------------------------------------------------------------

	/**
	 * Set the parent of this application context.
	 * <p>The parent {@linkplain ApplicationContext#getEnvironment() environment} is
	 * {@linkplain ConfigurableEnvironment#merge(ConfigurableEnvironment) merged} with
	 * this (child) application context environment if the parent is non-{@code null} and
	 * its environment is an instance of {@link ConfigurableEnvironment}.
	 * @see ConfigurableEnvironment#merge(ConfigurableEnvironment)
	 */
	//如果父级不是 null,并且其环境是ConfigurableEnvironment的实例,则设置当前ApplicationContext的父级。并 合并当前上下文的环境和父级上下文的环境
	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		this.parent = parent;
		if (parent != null) {
			Environment parentEnvironment = parent.getEnvironment();
			if (parentEnvironment instanceof ConfigurableEnvironment) {
				getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
			}
		}
	}

	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
		this.beanFactoryPostProcessors.add(postProcessor);
	}

	/**
	 * Return the list of BeanFactoryPostProcessors that will get applied
	 * to the internal BeanFactory.
	 */
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return this.beanFactoryPostProcessors;
	}

	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
		if (this.applicationEventMulticaster != null) {
			this.applicationEventMulticaster.addApplicationListener(listener);
		}
		this.applicationListeners.add(listener);
	}

	/**
	 * Return the list of statically specified ApplicationListeners.
	 */
	public Collection<ApplicationListener<?>> getApplicationListeners() {
		return this.applicationListeners;
	}

	//Spring容器的核心方法
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {//同步当前上下文的刷新，以保证其在刷新时是线程安全的
			//------------------------------ BeanFactory的创建及预准备工作  ------------------------------------------/
			//1 做容器刷新前的准备工作
			/*
				创建BeanFactory之前的一些准备工作，
					1.1设置容器启动的时间
					1.2设置容器关闭和开启的标志位
					1.3获取环境对象,并加载当前系统的环境属性到Environment对象中
					1.4设置监听器和对应事件的容器
				*/
			//-----------------------------[spring容器-前戏开始]------------------------
			prepareRefresh();

			//2 创建spring容器对象(DefaultListableBeanFactory) 同时加载配置文件的属性值到当前工厂中
			/*
				刷新bean工厂
					1.创建bean工厂
						1.1.如果之前有bean工厂则关闭并销毁
						1.2.创建空的DefaultListableBeanFactory
						1.3.设置该工厂的序列号id
						1.4.初始化工厂的属性（1.本工厂是否允许bean覆盖,2.本工厂是否允许bean中有循环依赖）
						1.5.将各种方式配置的bean,从配置的状态加载成beanDefinition放在bean工厂中--重要
					2.获取bean工厂
						2.1如果存在则返回该工厂对象
			 */
			//spring容器-初始化（创建）
			//加载Bean的定义信息到内存中
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			//3 初始化bean工厂
			/*对BeanFactory进行一些设置，比如设置spring容器的类加载器等*/
			//spring容器-初始化（赋值），对各种Bean工厂的属性进行公共的填充
			prepareBeanFactory(beanFactory);
			//-----------------------------[spring容器-前戏完成]------------------------

			try {
				//4 BeanFactory准备工作完成后的处理工作
				/* 抽象方法，子类可以通过重写这个方法来在BeanFactory创建并准备完成以后做进一步的设置*/
				//使用  当前的   bean工厂的BeanFactoryPostProcessor的能力
				postProcessBeanFactory(beanFactory);

				//5 使用  所有的  已经注册好的BeanFactoryPostProcessor 能力
				//调用实现BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor接口的实现类，
				//从而对Bean定义信息和Bean工厂进行处理和管理
				/**
					大部分bean定义信息在这一步之后就已经注册成功了

					一个小点：@ComponentScan @Component @Configuration @Bean
				    @Import @PropertySource @ImportSource @ComponentScans 等注解也是在这一步解析的
					SpringBoot的自动装配也是在这一步做的,
					而这些事主要都是
					{@link org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}中干的
				*/
				invokeBeanFactoryPostProcessors(beanFactory);
				//----------------------------------BeanFactory实例化+初始化完成---------------------------------------------------/

				//6 注册BeanPostProcessor到Spring容器中
				/* 将BPP的实现类实例化后放入BeanFactory容器中,他们将在创建bean的前后执行*/
				registerBeanPostProcessors(beanFactory);

				//7 为Spring上下文初始化MessageSource组件（做国际化功能；消息绑定，消息解析）；SpringMVC有具体实现
				initMessageSource();

				//8 初始化本地事件派发器
				initApplicationEventMulticaster();

				//9 子类重写这个方法，该方法将在容器刷新的时候处理自定义逻辑；如创建Tomcat，Jetty等WEB服务器
				/* 在Bean实例化之前自定义方法,钩子方法，springboot用于初始化中间件容器*/
				onRefresh();

				//10 注册本地监听器。就是注册实现了ApplicationListener接口的监听器bean
				//在所有注册的beanDefinition中查找listener bean,并将其注册到本地事件派发器中
				registerListeners();

				//11 实例化Bean，核心方法
				/*
					  1.初始化所有剩下的非懒加载的单例bean
					  2.初始化创建这些Bean
					  3.填充属性
					  4.初始化方法的调用（调用afterPropertiesSet方法、init-method方法）
					  5.调用BeanPostProcessor（后置处理器）对实例bean进行后置处理
				 */
				finishBeanFactoryInitialization(beanFactory);

				//12 完成上下文的刷新工作
				/* 发布公共事件，用于监听者接收*/
				/* 完成BeanFactory的初始化工作；IOC容器创建完成；*/
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// 销毁已创建的单例以避免资源占用。
				destroyBeans();
				// 重置“active”标志.
				cancelRefresh(ex);
				// Propagate exception to caller.
				throw ex;
			}

			finally {
				//13 在Spring的核心中重置常见的自省缓存，因为现在可能不再需要单例bean的元数据...
				resetCommonCaches();
			}
		}
	}

	/**
	 * 准备当前上下文的一些属性以便刷新例如：
	 * 设置它的启动日期和活动标志，以及执行属性源的任何初始化。
	 */
	//刷新前的预处理;
	protected void prepareRefresh() {
		// 设置开始时间，关闭状态为false，开启状态为true
		this.startupDate = System.currentTimeMillis();
		//spring容器的关闭状态为否
		this.closed.set(false);
		//spring容器的开启状态为是
		this.active.set(true);
		//到这里意味着spring容器开始了初始化
		//日志
		if (logger.isDebugEnabled()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Refreshing " + this);
			}
			else {
				logger.debug("Refreshing " + getDisplayName());
			}
		}

		// 初始化一些属性设置;
		/* 子类自定义的 属性源 方法；*/
		initPropertySources();

		/* 参见：ConfigurablePropertyResolver的setRequiredProperties();*/
		//初始化spring的环境,并且校验配置文件属性的合法性以及环境变量的合法性
		getEnvironment().validateRequiredProperties();

		//创建本地监听器的容器
		//这里是为了拓展实现的
		if (this.earlyApplicationListeners == null) {
			this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
		}
		else {
			//将本地监听器的容器重置为预刷新状态--也就是空的实现
			this.applicationListeners.clear();
			//初始化
			this.applicationListeners.addAll(this.earlyApplicationListeners);
		}

		// 保存容器中的一些事件
		//创建本地事件器的容器
		this.earlyApplicationEvents = new LinkedHashSet<>();
	}

	/**
	 * <p>用实际实例 替换任何 本身存在的属性源。
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#initServletPropertySources
	 */
	//在Spring容器构建的时候,为了子类能动态扩展初始化
	protected void initPropertySources() {
		// For subclasses: do nothing by default.
	}

	/**
	 * Tell the subclass to refresh the internal bean factory.
	 * @return the fresh BeanFactory instance
	 * @see #refreshBeanFactory()
	 * @see #getBeanFactory()
	 */
	// 创建了一个BeanFactory
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		refreshBeanFactory();//核心方法
		return getBeanFactory();//返回刚才创建的DefaultListableBeanFactory
	}

	/**
	 * 配置BeanFactory的标准context配置，例如上下文的ClassLoader和后处理器。
	 * @param beanFactory the BeanFactory to configure
	 */
	// BeanFactory的预准备工作
	/*
		初始化beanFactory
	*/
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		//1.为当前Bean工厂设置类加载器和SPEL表达式的解析器
		/* 设置BeanFactory的类加载器为当前上下文的类加载器*/
		beanFactory.setBeanClassLoader(getClassLoader());
		/* 设置Spring EL表达式解析器（Bean初始化完成后填充属性时会用到）*/
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));

		//2.设置属性解析注册器和BeanPostProcessor
		/* Bean工厂对Bean属性的管理(转换)的工具*/
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
		//给当前beanFactory设置一个BeanPostProcessor为[ApplicationContextAwareProcessor 此类用于完成某些Aware对象的注入]
		/* 将当前的ApplicationContext对象交给ApplicationContextAwareProcessor类来处理，从而可以在Aware接口实现类中注入applicationContext*/
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));

		//3.设置Bean工厂现在需要忽略的接口
		//设置忽略的自动装配的接口EnvironmentAware、EmbeddedValueResolverAware、xxx；这些接口的实现类不能通过类型来自动注入，因为它是被容器由set的方式注入的--ApplicationContextAwareProcessor已经将底下的aware处理了
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

		// 4.注册可以解析的自动装配组件；可以直接在任何组件中自动注入：
		/* BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext*/
		//注册后，这些接口功能可以在spring的任意组件中直接获取并使用
		//这里registerResolvableDependency意思是如果以下的这些key类出现了歧义，将优先使用value值，比如ResourceLoader就是this为优先推荐的，实现的功能相当于@Primary注解
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		beanFactory.registerResolvableDependency(ApplicationContext.class, this);

		// 5.添加BeanPostProcessor【ApplicationListenerDetector】后置处理器，在bean初始化前后的一些工作
		/* 如果当前的BeanFactory包含loadTimeWeaver这个Bean，则说明存在类加载期织入AspectJ，
		   那么就把当前BeanFactory交给类加载期BeanPostProcessor实现类LoadTimeWeaverAwareProcessor来处理，从而实现类加载期织入AspectJ的目的。*/
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

		// 6.添加编译时的AspectJ；
		//增加对aspectJ的支持，在Java中织入分为三种方式：1.编译器织入、2.类加载器织入、3.运行期织入，编译器织入指的是在Java编译器中采用特殊的编译器，将切面织入到Java类中
		//类加载器方式则是通过特殊的类加载器，在类的字节码加载到jvm中的时候，织入切面，运行期织入则是采用cglib和jdk代理的方式进行切面的织入
		//aspectJ提供了俩中织入方式，第一种是通过特殊的编译器，在编译器中，将aspectJ语言编写的切面类织入到Java类中，第二种则是在类加载期间织入，就是下面的load time weaving
		if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			//添加一个[LoadTimeWeaverAwareProcessor]BeanPostProcessor
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			/* 设置一个临时的ClassLoader以进行类型匹配。*/
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}


		//将默认的系统环境的bean添加到BeanFactory的一级缓存中

		// 7.给BeanFactory中注册一些能用的组件；
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
			/* 环境信息ConfigurableEnvironment*/
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}

		//8.给bean工厂注册系统的一些配置和变量
		//注册系统配置systemProperties组件Bean
		/* 系统属性，systemProperties【Map<String, Object>】*/
		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}
		//注册系统环境systemEnvironment组件Bean
		/* 系统环境变量systemEnvironment【Map<String, Object>】*/
		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for registering special
	 * BeanPostProcessors etc in certain ApplicationContext implementations.
	 * @param beanFactory the bean factory used by the application context
	 */
	//子类实现并重写这个方法以达到在BeanFactory创建后做一些事情
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	}

	/**
	 * <p>实例化<p/> 并 <p>调用</p> 所有已经注册的BeanFactoryPostProcessorBean，
	 * 如果给定的排序的话就遵循显式顺序，该方法在单例Bean被实例化之前被调用。
	 */
	//执行BeanFactoryPostProcessor的后置处理器方法
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		// 执行BeanFactoryPostProcessor的方法
		/* 调用所有实现了BeanDefinitionRegistryPostProcessor和 BeanFactoryPostProcessor接口的实现类方法*/
 		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}

	/**
	 * 不同接口类型的BeanPostProcessor；在Bean创建前后的执行时机是不一样的
	 * BeanPostProcessor、DestructionAwareBeanPostProcessor、InstantiationAwareBeanPostProcessor在createBean()方法中处理的，bean创建之前调用的
	 * SmartInstantiationAwareBeanPostProcessor、MergedBeanDefinitionPostProcessor【internalPostProcessors】 （bean创建完成后调用）
	 * @param beanFactory 容器对象
	 */
	/*
		实例化并注册所有的BeanPostProcessor
	 */
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		// 注册BeanPostProcessors到BeanFactory中
		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
	}

	//初始化MessageSource组件
	protected void initMessageSource() {
		// 1.从Spring当前上下文中获取Bean工厂
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		//2.判断当前bean工厂的beanDefinition中是否有自定义的MessageSource的beanDefinition，
		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
			//如果有则从bean工厂中获取到自定义的一个消息源的beanDefinition对应的bean对象
			//再将该自定义的消息源bean对象设置到当前bean工厂的消息源组件中
			this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
			//当父级bean工厂不为空,并且这个消息源组件是一个有层级的消息源组件
			if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
				HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
				//然后判断当前父级的消息源组件是否为空
				if (hms.getParentMessageSource() == null) {
					//如果为空，则将当前bean工厂的父级消息源组件赋值给这个有层级的消息源的父级消息源
					hms.setParentMessageSource(getInternalParentMessageSource());
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Using MessageSource [" + this.messageSource + "]");
			}
		}
		else {
			//3.如果没有自定义的消息源组件，则默认创建一个DelegatingMessageSource，并应用到当前bean工厂的消息源组件中
			// MessageSource作用：取出国际化配置文件中的某个key的值；能按照区域信息获取；
			DelegatingMessageSource dms = new DelegatingMessageSource();
			//设置父级消息源组件为当前bean工厂的父级消息源组件
			dms.setParentMessageSource(getInternalParentMessageSource());
			//将这里new出来的消息源应用给当前上下文的bean工厂的消息源组件中
			this.messageSource = dms;

			//将创建好的消息源组件注册到bean工厂中
			//把创建好的messageSource注册到容器中，以后获取国际化配置文件的值的时候，可以将注入MessageSource到使用的地方；
			//注入后通过这个方法使用MessageSource.getMessage(String code, Object[] args, String defaultMessage, Locale locale);
			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
			}
		}
	}

	//初始化事件派发器
	protected void initApplicationEventMulticaster() {
		//1.获取要刷新的Spring容器当前的BeanFactory
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		//2.判断Bean工厂中是否有applicationEventMulticaster 的这个beanDefinition ,
		// 如果有就从容器中获取，没有就去创建一个默认的事件派发器并注册到容器中的事件派发器中
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			//2.1从BeanFactory中获取applicationEventMulticaster的ApplicationEventMulticaster；
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			//2.2创建默认的SimpleApplicationEventMulticaster 类型的本地应用的事件派发器
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			//2.3将刚刚创建的事件派发器应用到Spring的bean工厂中，以后其他组件就可以直接自动注入
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Initialize the LifecycleProcessor.
	 * Uses DefaultLifecycleProcessor if none defined in the context.
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
			this.lifecycleProcessor =
					beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
			}
		}
		else {
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			this.lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " +
						"[" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
			}
		}
	}

	//子类重写AbstractApplicationContext.onRefresh()这个方法，在容器刷新的时候可以自定义逻辑；
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}

	//检查和注册本地监听器
	//将所有项目里面的ApplicationListener注册到容器中
	protected void registerListeners() {
		//1.从当前Spring上下文中拿到所有的ApplicationListener
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			//1.1将获取到的每个监听器添加到Spring上下文的事件派发器的监听器集合中
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// 2.根据类型从Spring上下文中的 bean工厂中 匹配到所有的ApplicationListener类型的bean名称
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			//2.1将每个监听器添加到事件派发器的监听器bean的集合中   --要延迟发布的监听器
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// prepareRefresh方法中 earlyApplicationEvents 中保存之前的事件，
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				//3.派发之前步骤产生的事件   --将之前prepareRefresh()方法最后注册的earlyApplicationEvent发布出去
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}

	/*
		1.完成上下文中bean工厂的初始化
		2.初始化所有剩余的单例bean。
	*/
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		//1.完成上下文中bean工厂的初始化
		// 1.1给容器设置类转换器,用来处理容器中对象的类型转换
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		// 1.2给容器添加值解析器,用来处理注解中的${xxx}，如果没有自定义属性解析器，将会创建一个默认的解析器应用在bean工厂中
		/*
		   如果之前没有任何beanPostProcessor进行过注册，
		   则注册一个默认的值解析器(这个Lambada表达式就是值解析器)：
		   此时，该解析器主要用于注解属性值的解析。
	    */
		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}

		//1.3处理aop织入的东西，尽早初始化loadTimeWeaverAware，以便尽早注册他们的转换器
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		//1.4设置容器的临时类加载器，禁止使用临时类加载器进行类型匹配
		beanFactory.setTempClassLoader(null);

		//1.5锁定容器不会再修改的配置，冻结所有的额beanDefinition，因为底下要开始用这些bean的定义信息了，他们不能在被用的时候被修改了
		beanFactory.freezeConfiguration();

		// 2.实例化以及初始化 剩下 没有懒加载能力的单例bean
		// 核心方法————实例化Bean
		beanFactory.preInstantiateSingletons();
	}

	/**
	 * Finish the refresh of this context, invoking the LifecycleProcessor's
	 * onRefresh() method and publishing the
	 * {@link org.springframework.context.event.ContextRefreshedEvent}.
	 */
	//完成BeanFactory的初始化创建工作；IOC容器就创建完成；刷新
	protected void finishRefresh() {
		//1.清除上下文级别的资源缓存（例如来自扫描的ASM元数据）。
		clearResourceCaches();

		/* 为此上下文初始化生命周期处理器*/
		//2.初始化生命周期有关的后置处理器，BeanFactory创建完成后刷新相关的工作
		/* 默认从容器中找是否有lifecycleProcessor的组件【LifecycleProcessor】；如果没有new DefaultLifecycleProcessor();加入到容器；*/
		initLifecycleProcessor();

		//3.首先将刷新完毕事件传播到生命周期处理器（触发isAutoStartup方法返回true的SmartLifecycle的start方法）
		getLifecycleProcessor().onRefresh();

		//4. 发布容器刷新完成事件；这个事件表示此时spring容器已经初始化完毕，许多框架会在这一步开始启动，例如springCloud-NaCos
		/* 推送上下文刷新完毕事件到相应的监听器*/
		publishEvent(new ContextRefreshedEvent(this));

		//5.暴露,将spring容器注册到LiveBeansView
		LiveBeansView.registerApplicationContext(this);
	}

	/**
	 * Cancel this context's refresh attempt, resetting the {@code active} flag
	 * after an exception got thrown.
	 * @param ex the exception that led to the cancellation
	 */
	protected void cancelRefresh(BeansException ex) {
		this.active.set(false);
	}

	/**
	 * Reset Spring's common reflection metadata caches, in particular the
	 * {@link ReflectionUtils}, {@link AnnotationUtils}, {@link ResolvableType}
	 * and {@link CachedIntrospectionResults} caches.
	 * @since 4.2
	 * @see ReflectionUtils#clearCache()
	 * @see AnnotationUtils#clearCache()
	 * @see ResolvableType#clearCache()
	 * @see CachedIntrospectionResults#clearClassLoader(ClassLoader)
	 */
	protected void resetCommonCaches() {
		//1.清理反射的缓存
		ReflectionUtils.clearCache();
		//2.清理注解的缓存
		AnnotationUtils.clearCache();
		//3.清理类型解析的缓存
		ResolvableType.clearCache();
		//4.清理类加载器
		CachedIntrospectionResults.clearClassLoader(getClassLoader());
	}


	/**
	 * Register a shutdown hook with the JVM runtime, closing this context
	 * on JVM shutdown unless it has already been closed at that time.
	 * <p>Delegates to {@code doClose()} for the actual closing procedure.
	 * @see Runtime#addShutdownHook
	 * @see #close()
	 * @see #doClose()
	 */
	@Override
	public void registerShutdownHook() {
		if (this.shutdownHook == null) {
			// No shutdown hook registered yet.
			this.shutdownHook = new Thread() {
				@Override
				public void run() {
					synchronized (startupShutdownMonitor) {
						doClose();
					}
				}
			};
			Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		}
	}

	/**
	 * Callback for destruction of this instance, originally attached
	 * to a {@code DisposableBean} implementation (not anymore in 5.0).
	 * <p>The {@link #close()} method is the native way to shut down
	 * an ApplicationContext, which this method simply delegates to.
	 * @deprecated as of Spring Framework 5.0, in favor of {@link #close()}
	 */
	@Deprecated
	public void destroy() {
		close();
	}

	/**
	 * 关闭此应用程序上下文，销毁其bean工厂中的所有bean。
	 * <p>代表{@code doClose（）}进行实际的关闭过程。
	 * 如果已注册JVM关闭钩子，那么就删除它，因为不再需要它。
	 * @see #doClose()
	 * @see #registerShutdownHook()
	 */
	@Override
	public void close() {
		synchronized (this.startupShutdownMonitor) {
			doClose();
			// 如果我们注册了JVM关闭钩子，则现在不再需要它：我们已经显式关闭了上下文。
			if (this.shutdownHook != null) {
				try {
					Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
				}
				catch (IllegalStateException ex) {
					// ignore - VM is already shutting down
				}
			}
		}
	}

	/**
	 * 实际执行上下文关闭：发布ContextClosedEvent并销毁此应用程序上下文的bean工厂中的单例。
	 * <p>由{@code close（）}和JVM关闭钩子（如果有）调用。
	 * @see org.springframework.context.event.ContextClosedEvent
	 * @see #destroyBeans()
	 * @see #close()
	 * @see #registerShutdownHook()
	 */
	protected void doClose() {
		// 检查是否需要实际的尝试关闭...
		if (this.active.get() && this.closed.compareAndSet(false, true)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Closing " + this);
			}

			LiveBeansView.unregisterApplicationContext(this);

			try {
				// 发布关闭事件。
				publishEvent(new ContextClosedEvent(this));
			}
			catch (Throwable ex) {
				logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
			}

			// 停止所有Lifecycle bean，以避免在单个销毁期间造成延迟。
			if (this.lifecycleProcessor != null) {
				try {
					this.lifecycleProcessor.onClose();
				}
				catch (Throwable ex) {
					logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
				}
			}

			//销毁上下文的BeanFactory中所有缓存的单例。
			destroyBeans();

			// 关闭此上下文本身的状态。
			closeBeanFactory();

			// 如果愿意，让子类做一些最后的清理...
			onClose();

			// 将本地应用程序侦听器重置为预刷新状态。
			if (this.earlyApplicationListeners != null) {
				this.applicationListeners.clear();
				this.applicationListeners.addAll(this.earlyApplicationListeners);
			}

			// 切换为非活动状态。
			this.active.set(false);
		}
	}

	/**
	 * Template method for destroying all beans that this context manages.
	 * The default implementation destroy all cached singletons in this context,
	 * invoking {@code DisposableBean.destroy()} and/or the specified
	 * "destroy-method".
	 * <p>Can be overridden to add context-specific bean destruction steps
	 * right before or right after standard singleton destruction,
	 * while the context's BeanFactory is still active.
	 * @see #getBeanFactory()
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
	 */
	protected void destroyBeans() {
		getBeanFactory().destroySingletons();
	}

	/**
	 * Template method which can be overridden to add context-specific shutdown work.
	 * The default implementation is empty.
	 * <p>Called at the end of {@link #doClose}'s shutdown procedure, after
	 * this context's BeanFactory has been closed. If custom shutdown logic
	 * needs to execute while the BeanFactory is still active, override
	 * the {@link #destroyBeans()} method instead.
	 */
	protected void onClose() {
		// For subclasses: do nothing by default.
	}

	@Override
	public boolean isActive() {
		return this.active.get();
	}

	/**
	 * Assert that this context's BeanFactory is currently active,
	 * throwing an {@link IllegalStateException} if it isn't.
	 * <p>Invoked by all {@link BeanFactory} delegation methods that depend
	 * on an active context, i.e. in particular all bean accessor methods.
	 * <p>The default implementation checks the {@link #isActive() 'active'} status
	 * of this context overall. May be overridden for more specific checks, or for a
	 * no-op if {@link #getBeanFactory()} itself throws an exception in such a case.
	 */
	protected void assertBeanFactoryActive() {
		if (!this.active.get()) {
			if (this.closed.get()) {
				throw new IllegalStateException(getDisplayName() + " has been closed already");
			}
			else {
				throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
			}
		}
	}


	//---------------------------------------------------------------------
	//  BeanFactory接口的实现
	//---------------------------------------------------------------------

	@Override
	public Object getBean(String name) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType, args);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	@Nullable
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		return getBeanFactory().getAliases(name);
	}


	//---------------------------------------------------------------------
	// ListableBeanFactory接口的实现
	//---------------------------------------------------------------------

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForAnnotation(annotationType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException {

		assertBeanFactoryActive();
		return getBeanFactory().getBeansWithAnnotation(annotationType);
	}

	@Override
	@Nullable
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {

		assertBeanFactoryActive();
		return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
	}


	//---------------------------------------------------------------------
	// HierarchicalBeanFactory接口的实现
	//---------------------------------------------------------------------

	@Override
	@Nullable
	public BeanFactory getParentBeanFactory() {
		return getParent();
	}

	@Override
	public boolean containsLocalBean(String name) {
		return getBeanFactory().containsLocalBean(name);
	}

	/**
	 * Return the internal bean factory of the parent context if it implements
	 * ConfigurableApplicationContext; else, return the parent context itself.
	 * @see org.springframework.context.ConfigurableApplicationContext#getBeanFactory
	 */
	@Nullable
	protected BeanFactory getInternalParentBeanFactory() {
		return (getParent() instanceof ConfigurableApplicationContext ?
				((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
	}


	//---------------------------------------------------------------------
	// MessageSource接口的实现
	//---------------------------------------------------------------------

	@Override
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(code, args, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(resolvable, locale);
	}

	/**
	 * Return the internal MessageSource used by the context.
	 * @return the internal MessageSource (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	private MessageSource getMessageSource() throws IllegalStateException {
		if (this.messageSource == null) {
			throw new IllegalStateException("MessageSource not initialized - " +
					"call 'refresh' before accessing messages via the context: " + this);
		}
		return this.messageSource;
	}

	/**
	 * Return the internal message source of the parent context if it is an
	 * AbstractApplicationContext too; else, return the parent context itself.
	 */
	@Nullable
	protected MessageSource getInternalParentMessageSource() {
		return (getParent() instanceof AbstractApplicationContext ?
				((AbstractApplicationContext) getParent()).messageSource : getParent());
	}


	//---------------------------------------------------------------------
	// ResourcePatternResolver接口的实现
	//---------------------------------------------------------------------

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return this.resourcePatternResolver.getResources(locationPattern);
	}


	//---------------------------------------------------------------------
	// 生命周期接口的实现
	//---------------------------------------------------------------------

	@Override
	public void start() {
		getLifecycleProcessor().start();
		publishEvent(new ContextStartedEvent(this));
	}

	@Override
	public void stop() {
		getLifecycleProcessor().stop();
		publishEvent(new ContextStoppedEvent(this));
	}

	@Override
	public boolean isRunning() {
		return (this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning());
	}


	//---------------------------------------------------------------------
	// 子类必须实现的抽象方法
	//---------------------------------------------------------------------

	/**
	 * Subclasses must implement this method to perform the actual configuration load.
	 * The method is invoked by {@link #refresh()} before any other initialization work.
	 * <p>A subclass will either create a new bean factory and hold a reference to it,
	 * or return a single BeanFactory instance that it holds. In the latter case, it will
	 * usually throw an IllegalStateException if refreshing the context more than once.
	 * @throws BeansException if initialization of the bean factory failed
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

	/**
	 * Subclasses must implement this method to release their internal bean factory.
	 * This method gets invoked by {@link #close()} after all other shutdown work.
	 * <p>Should never throw an exception but rather log shutdown failures.
	 */
	protected abstract void closeBeanFactory();

	/**
	 * Subclasses must return their internal bean factory here. They should implement the
	 * lookup efficiently, so that it can be called repeatedly without a performance penalty.
	 * <p>Note: Subclasses should check whether the context is still active before
	 * returning the internal bean factory. The internal factory should generally be
	 * considered unavailable once the context has been closed.
	 * @return this application context's internal bean factory (never {@code null})
	 * @throws IllegalStateException if the context does not hold an internal bean factory yet
	 * (usually if {@link #refresh()} has never been called) or if the context has been
	 * closed already
	 * @see #refreshBeanFactory()
	 * @see #closeBeanFactory()
	 */
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;


	/**
	 * Return information about this context.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getDisplayName());
		sb.append(", started on ").append(new Date(getStartupDate()));
		ApplicationContext parent = getParent();
		if (parent != null) {
			sb.append(", parent: ").append(parent.getDisplayName());
		}
		return sb.toString();
	}

}
