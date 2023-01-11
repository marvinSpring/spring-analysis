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

package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

/**
 * Support base class for singleton registries which need to handle
 * {@link org.springframework.beans.factory.FactoryBean} instances,
 * integrated with {@link DefaultSingletonBeanRegistry}'s singleton management.
 *
 * <p>Serves as base class for {@link AbstractBeanFactory}.
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

	/** 由FactoryBean创建的单例对象缓存:key:FactoryBean名称,val:对象. */
	//类似于singletonObjects，一级缓存，，它缓存的是factoryBean派生的对象
	private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>(16);


	/**
	 * Determine the type for the given FactoryBean.
	 * @param factoryBean the FactoryBean instance to check
	 * @return the FactoryBean's object type,
	 * or {@code null} if the type cannot be determined yet
	 */
	//获取工厂bean派生的bean对象的类型
	@Nullable
	protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
		try {
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged(
						(PrivilegedAction<Class<?>>) factoryBean::getObjectType, getAccessControlContext());
			}
			else {
				return factoryBean.getObjectType();
			}
		}
		catch (Throwable ex) {
			// Thrown from the FactoryBean's getObjectType implementation.
			logger.info("FactoryBean threw exception from getObjectType, despite the contract saying " +
					"that it should return null if the type of its object cannot be determined yet", ex);
			return null;
		}
	}

	/**
	 * Obtain an object to expose from the given FactoryBean, if available
	 * in cached form. Quick check for minimal synchronization.
	 * @param beanName the name of the bean
	 * @return the object obtained from the FactoryBean,
	 * or {@code null} if not available
	 */
	//从工厂bean的缓存中获取被派生的bean
	@Nullable
	protected Object getCachedObjectForFactoryBean(String beanName) {
		return this.factoryBeanObjectCache.get(beanName);
	}

	/**
	 * O从给定的 FactoryBean 获取要暴露的对象。
	 * @param factory 工厂Bean对象
	 * @param beanName Bean的名字
	 * @param shouldPostProcess Bean 是否应该进行后置增强器处理
	 * @return 从工厂Bean中获得的对象
	 * @throws BeanCreationException 如果工厂Bean对象创建失败
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
		//如果工厂bean是单例工厂bean 并且默认的单例bean注册器中包含单例beanName
		if (factory.isSingleton() && containsSingleton(beanName)) {
			//同步锁住 单例对象的一级缓存
			synchronized (getSingletonMutex()) {
				//从工厂bean的缓存中获取该bean
				Object object = this.factoryBeanObjectCache.get(beanName);
				if (object == null) {
					//如果获取不到则证明是第一次获取,真正的根据工厂bean去获取，它将从对应工厂的getObject中尝试获取beanName对应的bean对象--核心方法
					object = doGetObjectFromFactoryBean(factory, beanName);
					// 只有在上面的getObject()调用期间没有放在工厂bean对象的缓存中，
					// 则仅进行后处理和存储（例如，由于自定义getBean调用触发的循环引用处理）
					Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
					//如果已经存在则不做处理
					if (alreadyThere != null) {
						object = alreadyThere;
					}
					else {
						//如果不存在于缓存中，说明它需要被后置处理
						if (shouldPostProcess) {
							//如果这个单例当前正在创建中，说明还不能进行后置处理，先返回
							if (isSingletonCurrentlyInCreation(beanName)) {
								// 暂时返回未后处理的对象，尚未存储它。
								return object;
							}
							//在factoryBean中派生前执行处理
							beforeSingletonCreation(beanName);
							try {
								//增强处理工厂bean
								object = postProcessObjectFromFactoryBean(object, beanName);
							}
							catch (Throwable ex) {
								throw new BeanCreationException(beanName,
										"Post-processing of FactoryBean's singleton object failed", ex);
							}
							finally {
								//在factoryBean中派生后执行处理
								afterSingletonCreation(beanName);
							}
						}
						//如果是单例对象
						if (containsSingleton(beanName)) {
							//将当前派生好的bean对象则存入factoryBean的缓存中
							this.factoryBeanObjectCache.put(beanName, object);
						}
					}
				}
				return object;
			}
		}
		else {
			//真正的根据beanName在工厂中或获取派生的bean对象
			Object object = doGetObjectFromFactoryBean(factory, beanName);
			if (shouldPostProcess) {
				try {
					//增强处理factoryBean派生bean的能力
					object = postProcessObjectFromFactoryBean(object, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
				}
			}
			return object;
		}
	}

	/**
	 * 从给定的 FactoryBean 获取要暴露出来的Bean对象.
	 * @param factory FactoryBean的实例
	 * @param beanName bean名称
	 * @return 从工厂Bean获得的Bean对象
	 * @throws BeanCreationException 如果工厂Bean对象创建失败
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
		Object object;
		try {
			//如果系统安全管理器不为空
			if (System.getSecurityManager() != null) {
				//获取访问控制器
				AccessControlContext acc = getAccessControlContext();
				try {
					object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
				//从FactoryBean中的getObject方法中获取FactoryBean生产的Bean对象
				object = factory.getObject();
			}
		}
		catch (FactoryBeanNotInitializedException ex) {
			throw new BeanCurrentlyInCreationException(beanName, ex.toString());
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
		}

		// 不接受尚未完全初始化的 FactoryBean 的null值：
		// 许多 FactoryBeans 只是返null空值。

		//如果factoryBean的getObject只返回null值，那么将缓存一个NullBean
		if (object == null) {
			if (isSingletonCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(
						beanName, "FactoryBean which is currently in creation returned null from getObject");
			}
			object = new NullBean();
		}
		return object;
	}

	/**
	 * Post-process the given object that has been obtained from the FactoryBean.
	 * The resulting object will get exposed for bean references.
	 * <p>The default implementation simply returns the given object as-is.
	 * Subclasses may override this, for example, to apply post-processors.
	 * @param object the object obtained from the FactoryBean.
	 * @param beanName the name of the bean
	 * @return the object to expose
	 * @throws org.springframework.beans.BeansException if any post-processing failed
	 */
	//增强处理factoryBean派生的bean的逻辑
	protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
		return object;
	}

	/**
	 * Get a FactoryBean for the given bean if possible.
	 * @param beanName the name of the bean
	 * @param beanInstance the corresponding bean instance
	 * @return the bean instance as FactoryBean
	 * @throws BeansException if the given bean cannot be exposed as a FactoryBean
	 */
	//根据bean名称和bean对象获取其所属的factoryBean
	protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
		if (!(beanInstance instanceof FactoryBean)) {
			throw new BeanCreationException(beanName,
					"Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
		}
		return (FactoryBean<?>) beanInstance;
	}

	//------------------------
	//  实现默认的单例bean注册器的能力
	//------------------------

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	//根据bean名称吧bean对象从三级缓存中移除并将其从工厂bean的缓存中获取
	@Override
	protected void removeSingleton(String beanName) {
		synchronized (getSingletonMutex()) {
			super.removeSingleton(beanName);
			this.factoryBeanObjectCache.remove(beanName);
		}
	}

	/**
	 * Overridden to clear the FactoryBean object cache as well.
	 */
	//将所有的单例bean缓存都移除
	@Override
	protected void clearSingletonCache() {
		synchronized (getSingletonMutex()) {
			super.clearSingletonCache();
			this.factoryBeanObjectCache.clear();
		}
	}

	/**
	 * Return the security context for this bean factory. If a security manager
	 * is set, interaction with the user code will be executed using the privileged
	 * of the security context returned by this method.
	 * @see AccessController#getContext()
	 */
	//获取访问控制器的上下文
	protected AccessControlContext getAccessControlContext() {
		return AccessController.getContext();
	}

}
