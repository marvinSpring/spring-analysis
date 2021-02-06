/*
 * Copyright 2002-2014 the original author or authors.
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

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * Spring的核心接口，为应用程序提供上下文配置。
 * 一般情况下本接口是只读的，
 * 但是如果实现本接口就可以重载。
 *
 * <p>ApplicationContext提供：
 * <ul>
	 * <li>用于访问应用程序组件的BeanFactory。————{@link org.springframework.beans.factory.ListableBeanFactory}.
	 * <li>以通用方式加载文件资源。————{@link org.springframework.core.io.ResourceLoader} interface.
	 * <li>将事件发布给注册的Listener。————{@link ApplicationEventPublisher} interface.
	 * <li>解决消息的能力，支持国际化。————{@link MessageSource} interface.
 	 * 有上面这些接口，我Application Context就拥有了N多的功能，各种能力
	 * <li>从父级Context继承. 在后代Context中的定义将始终优先。
 	 * 例如：
 		* 这意味着整个Web应用程序都可以使用单个父上下文，而每个servlet都有其自己的子上下文，该子上下文独立于任何其他servlet的子上下文。
 * </ul>
 *
 * <p>除了标准的{@link org.springframework.beans.factory.BeanFactory}生命周期功能之外，
 * ApplicationContext实现还检测并调用{@link ApplicationContextAware} bean以及{@link ResourceLoaderAware}，
 * {@link ApplicationEventPublisherAware}和{@link MessageSourceAware } 。
 * 除了标准的Spring生命周期之外还会有上面这些aware接口的执行
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
//Spring真正的总接口
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * Return the unique id of this application context.
	 * @return the unique id of the context, or {@code null} if none
	 */
	//当前的上下文Id
	@Nullable
	String getId();

	/**
	 * Return a name for the deployed application that this context belongs to.
	 * @return a name for the deployed application, or the empty String by default
	 */
	//当前上下文的名称
	String getApplicationName();

	/**
	 * Return a friendly name for this context.
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * 返回第一次加载此上下文时的时间戳
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * 返回父级上下文，如果没有父级，则返回{@code null}，这是上下文层次结构的根。
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	/**
	 * 针对此上下文公开AutowireCapableBeanFactory功能。
	 * <p>应用程序代码通常不使用此选项，除非是为了初始化遗留在应用程序上下文之外的bean实例，
	 * 将Spring bean生命周期（全部或部分）应用于它们。
	 * <p>或者，内部BeanFactory由
	 * {@link ConfigurableApplicationContext}接口也提供对{@link AutowireCapableBeanFactory}接口的访问。
	 * 本方法主要用作ApplicationContext接口上的一种方便的特定功能。
	 *
	 * <p><b>注意：从4.2开始，此方法将在关闭应用程序上下文后始终引发IllegalStateException。
	 * <b>在当前的Spring Framework版本中，只有可刷新的应用程序上下文才具有这种行为；
	 * 从4.2开始，将要求所有应用程序上下文实现都必须遵守。
	 * @return 当前Context的 {@link AutowireCapableBeanFactory}
	 * @throws IllegalStateException 如果上下文不支持
	 * {@link AutowireCapableBeanFactory} 接口，或尚不具有支持自动装配功能的Bean工厂（例如，如果从未调用{@code #refresh()}），或者上下文已经关闭
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
