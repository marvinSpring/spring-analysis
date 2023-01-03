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

package org.springframework.context.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link ApplicationEventMulticaster} interface,
 * providing the basic listener registration facility.
 *
 * <p>Doesn't permit multiple instances of the same listener by default,
 * as it keeps listeners in a linked Set. The collection class used to hold
 * ApplicationListener objects can be overridden through the "collectionClass"
 * bean property.
 *
 * <p>Implementing ApplicationEventMulticaster's actual {@link #multicastEvent} method
 * is left to subclasses. {@link SimpleApplicationEventMulticaster} simply multicasts
 * all events to all registered listeners, invoking them in the calling thread.
 * Alternative implementations could be more sophisticated in those respects.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 1.2.3
 * @see #getApplicationListeners(ApplicationEvent, ResolvableType)
 * @see SimpleApplicationEventMulticaster
 */
public abstract class AbstractApplicationEventMulticaster
		implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {

	//监听器的帮助类，类似于自己的util，用于存放程序中的监听器集合，preFiltered标识是否需要预过滤监听器
	private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);

	//ListenerCacheKey是基于事件类型和源类型的类作为Key用来存储不同事件类型对应的监听器帮助类,不同事件类型会有不同的一些监听器集合
	final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);

	@Nullable
	private ClassLoader beanClassLoader;

	@Nullable
	private BeanFactory beanFactory;

	//互斥的监听器帮助类
	private Object retrievalMutex = this.defaultRetriever;


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		if (beanFactory instanceof ConfigurableBeanFactory) {
			ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
			if (this.beanClassLoader == null) {
				this.beanClassLoader = cbf.getBeanClassLoader();
			}
			this.retrievalMutex = cbf.getSingletonMutex();
		}
	}

	private BeanFactory getBeanFactory() {
		if (this.beanFactory == null) {
			throw new IllegalStateException("ApplicationEventMulticaster cannot retrieve listener beans " +
					"because it is not associated with a BeanFactory");
		}
		return this.beanFactory;
	}


	//添加本地应用监听器到监听器集合中
	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		//锁定当前监听器帮助类,防止其他线程这会给这个监听器帮助类中赛监听器
		synchronized (this.retrievalMutex) {
			// Explicitly remove target for a proxy, if registered already,
			// in order to avoid double invocations of the same listener.
			//如果当前需要添加的监听器被已经注册了,则显示的将旧的注册过的监听器移除掉
			Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
			if (singletonTarget instanceof ApplicationListener) {
				this.defaultRetriever.applicationListeners.remove(singletonTarget);
			}
			//新增需要添加的监听器
			this.defaultRetriever.applicationListeners.add(listener);
			//清空监听器助手缓存Map
			this.retrieverCache.clear();
		}
	}

	@Override
	public void addApplicationListenerBean(String listenerBeanName) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
			this.retrieverCache.clear();
		}
	}

	@Override
	public void removeApplicationListener(ApplicationListener<?> listener) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.remove(listener);
			this.retrieverCache.clear();
		}
	}

	@Override
	public void removeApplicationListenerBean(String listenerBeanName) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
			this.retrieverCache.clear();
		}
	}

	@Override
	public void removeAllListeners() {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.clear();
			this.defaultRetriever.applicationListenerBeans.clear();
			this.retrieverCache.clear();
		}
	}


	/**
	 * Return a Collection containing all ApplicationListeners.
	 * @return a Collection of ApplicationListeners
	 * @see org.springframework.context.ApplicationListener
	 */
	protected Collection<ApplicationListener<?>> getApplicationListeners() {
		synchronized (this.retrievalMutex) {
			return this.defaultRetriever.getApplicationListeners();
		}
	}

	/**
	 * Return a Collection of ApplicationListeners matching the given
	 * event type. Non-matching listeners get excluded early.
	 * @param event the event to be propagated. Allows for excluding
	 * non-matching listeners early, based on cached matching information.
	 * @param eventType the event type
	 * @return a Collection of ApplicationListeners
	 * @see org.springframework.context.ApplicationListener
	 */
	protected Collection<ApplicationListener<?>> getApplicationListeners(
			ApplicationEvent event, ResolvableType eventType) {

		Object source = event.getSource();
		Class<?> sourceType = (source != null ? source.getClass() : null);
		ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

		// Quick check for existing entry on ConcurrentHashMap...
		ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
		if (retriever != null) {
			return retriever.getApplicationListeners();
		}

		if (this.beanClassLoader == null ||
				(ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) &&
						(sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
			// Fully synchronized building and caching of a ListenerRetriever
			synchronized (this.retrievalMutex) {
				retriever = this.retrieverCache.get(cacheKey);
				if (retriever != null) {
					return retriever.getApplicationListeners();
				}
				retriever = new ListenerRetriever(true);
				Collection<ApplicationListener<?>> listeners =
						retrieveApplicationListeners(eventType, sourceType, retriever);
				this.retrieverCache.put(cacheKey, retriever);
				return listeners;
			}
		}
		else {
			// No ListenerRetriever caching -> no synchronization necessary
			return retrieveApplicationListeners(eventType, sourceType, null);
		}
	}

	/**
	 * Actually retrieve the application listeners for the given event and source type.
	 * @param eventType the event type
	 * @param sourceType the event source type
	 * @param retriever the ListenerRetriever, if supposed to populate one (for caching purposes)
	 * @return the pre-filtered list of application listeners for the given event and source type
	 */
	private Collection<ApplicationListener<?>> retrieveApplicationListeners(
			ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever) {

		List<ApplicationListener<?>> allListeners = new ArrayList<>();
		Set<ApplicationListener<?>> listeners;
		Set<String> listenerBeans;
		synchronized (this.retrievalMutex) {
			listeners = new LinkedHashSet<>(this.defaultRetriever.applicationListeners);
			listenerBeans = new LinkedHashSet<>(this.defaultRetriever.applicationListenerBeans);
		}
		for (ApplicationListener<?> listener : listeners) {
			if (supportsEvent(listener, eventType, sourceType)) {
				if (retriever != null) {
					retriever.applicationListeners.add(listener);
				}
				allListeners.add(listener);
			}
		}
		if (!listenerBeans.isEmpty()) {
			BeanFactory beanFactory = getBeanFactory();
			for (String listenerBeanName : listenerBeans) {
				try {
					Class<?> listenerType = beanFactory.getType(listenerBeanName);
					if (listenerType == null || supportsEvent(listenerType, eventType)) {
						ApplicationListener<?> listener =
								beanFactory.getBean(listenerBeanName, ApplicationListener.class);
						if (!allListeners.contains(listener) && supportsEvent(listener, eventType, sourceType)) {
							if (retriever != null) {
								if (beanFactory.isSingleton(listenerBeanName)) {
									retriever.applicationListeners.add(listener);
								}
								else {
									retriever.applicationListenerBeans.add(listenerBeanName);
								}
							}
							allListeners.add(listener);
						}
					}
				}
				catch (NoSuchBeanDefinitionException ex) {
					// Singleton listener instance (without backing bean definition) disappeared -
					// probably in the middle of the destruction phase
				}
			}
		}
		AnnotationAwareOrderComparator.sort(allListeners);
		if (retriever != null && retriever.applicationListenerBeans.isEmpty()) {
			retriever.applicationListeners.clear();
			retriever.applicationListeners.addAll(allListeners);
		}
		return allListeners;
	}

	/**
	 * Filter a listener early through checking its generically declared event
	 * type before trying to instantiate it.
	 * <p>If this method returns {@code true} for a given listener as a first pass,
	 * the listener instance will get retrieved and fully evaluated through a
	 * {@link #supportsEvent(ApplicationListener, ResolvableType, Class)} call afterwards.
	 * @param listenerType the listener's type as determined by the BeanFactory
	 * @param eventType the event type to check
	 * @return whether the given listener should be included in the candidates
	 * for the given event type
	 */
	protected boolean supportsEvent(Class<?> listenerType, ResolvableType eventType) {
		if (GenericApplicationListener.class.isAssignableFrom(listenerType) ||
				SmartApplicationListener.class.isAssignableFrom(listenerType)) {
			return true;
		}
		ResolvableType declaredEventType = GenericApplicationListenerAdapter.resolveDeclaredEventType(listenerType);
		return (declaredEventType == null || declaredEventType.isAssignableFrom(eventType));
	}

	/**
	 * Determine whether the given listener supports the given event.
	 * <p>The default implementation detects the {@link SmartApplicationListener}
	 * and {@link GenericApplicationListener} interfaces. In case of a standard
	 * {@link ApplicationListener}, a {@link GenericApplicationListenerAdapter}
	 * will be used to introspect the generically declared type of the target listener.
	 * @param listener the target listener to check
	 * @param eventType the event type to check against
	 * @param sourceType the source type to check against
	 * @return whether the given listener should be included in the candidates
	 * for the given event type
	 */
	protected boolean supportsEvent(
			ApplicationListener<?> listener, ResolvableType eventType, @Nullable Class<?> sourceType) {

		GenericApplicationListener smartListener = (listener instanceof GenericApplicationListener ?
				(GenericApplicationListener) listener : new GenericApplicationListenerAdapter(listener));
		return (smartListener.supportsEventType(eventType) && smartListener.supportsSourceType(sourceType));
	}


	/**
	 * Cache key for ListenerRetrievers, based on event type and source type.
	 */
	private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

		private final ResolvableType eventType;

		@Nullable
		private final Class<?> sourceType;

		public ListenerCacheKey(ResolvableType eventType, @Nullable Class<?> sourceType) {
			Assert.notNull(eventType, "Event type must not be null");
			this.eventType = eventType;
			this.sourceType = sourceType;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			ListenerCacheKey otherKey = (ListenerCacheKey) other;
			return (this.eventType.equals(otherKey.eventType) &&
					ObjectUtils.nullSafeEquals(this.sourceType, otherKey.sourceType));
		}

		@Override
		public int hashCode() {
			return this.eventType.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.sourceType);
		}

		@Override
		public String toString() {
			return "ListenerCacheKey [eventType = " + this.eventType + ", sourceType = " + this.sourceType + "]";
		}

		@Override
		public int compareTo(ListenerCacheKey other) {
			int result = this.eventType.toString().compareTo(other.eventType.toString());
			if (result == 0) {
				if (this.sourceType == null) {
					return (other.sourceType == null ? 0 : -1);
				}
				if (other.sourceType == null) {
					return 1;
				}
				result = this.sourceType.getName().compareTo(other.sourceType.getName());
			}
			return result;
		}
	}


	/**
	 * 监听器帮助类、用于封装(不同事件类型或者不同源类型的)目标监听器的助手类，它被允许有效的检索过滤器去预过滤监听器，
	 *
	 * Helper class that encapsulates a specific set of target listeners,
	 * allowing for efficient retrieval of pre-filtered listeners.
	 * <p>An instance of this helper gets cached per event type and source type.
	 */
	private class ListenerRetriever {

		//存放应用程序事件监听器
		public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

		//存放应用程序监听器的bean名称
		public final Set<String> applicationListenerBeans = new LinkedHashSet<>();

		//是否预过滤监听器
		private final boolean preFiltered;

		public ListenerRetriever(boolean preFiltered) {
			this.preFiltered = preFiltered;
		}

		//获取 this 对应事件类型的 应用程序的事件监听器
		public Collection<ApplicationListener<?>> getApplicationListeners() {

			//创建指定空间大小的本地应用程序监听器集合
			List<ApplicationListener<?>> allListeners = new ArrayList<>(
					this.applicationListeners.size() + this.applicationListenerBeans.size());

			//将this对应事件类型的所有本地应用程序监听器的集合存入该集合
			allListeners.addAll(this.applicationListeners);

			//如果本地应用程序监听器的bean名称不为空
			if (!this.applicationListenerBeans.isEmpty()) {

				//获取当前bean工厂
				BeanFactory beanFactory = getBeanFactory();

				//将当前的这些本地应用程序监听器实例化并初始化
				for (String listenerBeanName : this.applicationListenerBeans) {
					try {
						ApplicationListener<?> listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);

						//也添加到allListeners集合中
						if (this.preFiltered || !allListeners.contains(listener)) {
							allListeners.add(listener);
						}
					}
					catch (NoSuchBeanDefinitionException ex) {
						// Singleton listener instance (without backing bean definition) disappeared -
						// probably in the middle of the destruction phase
					}
				}
			}
			//如果这个事件类型对应的监听器助手不需要预过滤处理并且对应的事件监听器的bean名称不为空,则给这些监听器进行一个排序
			if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
				AnnotationAwareOrderComparator.sort(allListeners);
			}
			return allListeners;
		}
	}

}
