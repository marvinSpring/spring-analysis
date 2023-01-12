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

package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/**
 * 本接口用于定义factoryBean<Bean>对象，可以派生对应的Bean对象
 *
 * 由{@link BeanFactory}中使用的对象实现的接口，
 * 这些对象本身就是单个对象的工厂。如果Bean实现此接口，
 * 则它将用作对象生产的工厂，而不是直接将FactoryBean用作实例。
 *
 * <p><b>注意：实现此接口的bean不能用作普通bean。
 * <b> FactoryBean是用bean样式定义的，
 * 但为bean引用公开的对象（{@link #getObject()}）始终是该对象创建。
 *
 * <p>FactoryBeans可以支持单例和多例，并且可以按需延迟创建对象，
 * 也可以在启动时急于创建对象。{@link SmartFactoryBean}接口允许公开更细粒度的行为元数据。
 *
 * <p>此接口在框架本身中大量使用，例如用于AOP {@link org.springframework.aop.framework.ProxyFactoryBean}或{@link org.springframework.jndi.JndiObjectFactoryBean}。
 * 它也可以用于自定义组件。但是，这仅在基础结构代码中很常见。
 *
 * <p><b>{@code FactoryBean}是程序性合同。实现不应依赖于注释驱动的注入或其他反射性工具。
 * <b> {@link #getObjectType())} {@link #getObject()}调用可能会在引导过程的早期到达，
 * 即使在任何后处理器设置之前也是如此。
 * 如果您需要访问其他bean，请实现{@link BeanFactoryAware}并以编程方式获取它们。
 *
 * <p> <b>该容器仅负责管理FactoryBean实例的生命周期，而不负责管理由FactoryBean创建的对象的生命周期。
 * <b>因此，对公开的bean对象（例如{@link java.io.Closeableclose()}将自动被<i> not <i>调用，
 * 相反，FactoryBean应该实现{@link DisposableBean}并将任何此类close调用委托给基础对象。
 *
 * <p>最后，FactoryBean对象参与包含BeanFactory的Bean创建同步。
 * 除了出于FactoryBean自身（或类似方式）内部的延迟初始化的目的之外，通常不需要内部同步。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 08.03.2003
 * @param <T> the bean type
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 */
public interface FactoryBean<T> {

	/**
	 * 返回此工厂管理的对象的实例（可能是共享的或独立的）。
	 * <p>与{@link BeanFactory}一样，它允许同时支持Singleton和Prototype设计模式。
	 * <p>如果在调用时此FactoryBean尚未完全初始化（例如，因为它包含在循环引用中），则
	 * 抛出相应的{@link FactoryBeanNotInitializedException}。
	 * <p>从Spring 2.0开始，FactoryBeans被允许返回{@code null}对象。工厂会将其视为正常值使用；
	 * it will not throw a FactoryBeanNotInitializedException in this case anymore.
	 * FactoryBean implementations are encouraged to throw
	 * FactoryBeanNotInitializedException themselves now, as appropriate.
	 * @return an instance of the bean (can be {@code null})
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	//获取bean的对象
	@Nullable
	T getObject() throws Exception;

	/**
	 *返回此FactoryBean创建的对象的类型；如果事先未知，则返回{@code null}。
	 * <p>This allows one to check for specific types of beans without
	 * instantiating objects, for example on autowiring.
	 * <p>In the case of implementations that are creating a singleton object,
	 * this method should try to avoid singleton creation as far as possible;
	 * it should rather estimate the type in advance.
	 * For prototypes, returning a meaningful type here is advisable too.
	 * <p>This method can be called <i>before</i> this FactoryBean has
	 * been fully initialized. It must not rely on state created during
	 * initialization; of course, it can still use such state if available.
	 * <p><b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return
	 * {@code null} here. Therefore it is highly recommended to implement
	 * this method properly, using the current state of the FactoryBean.
	 * @return the type of object that this FactoryBean creates,
	 * or {@code null} if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	//获取Bean对象的class类型
	@Nullable
	Class<?> getObjectType();

	/**
	 * Is the object managed by this factory a singleton? That is,
	 * will {@link #getObject()} always return the same object
	 * (a reference that can be cached)?
	 * <p><b>NOTE:</b> If a FactoryBean indicates to hold a singleton object,
	 * the object returned from {@code getObject()} might get cached
	 * by the owning BeanFactory. Hence, do not return {@code true}
	 * unless the FactoryBean always exposes the same reference.
	 * <p>The singleton status of the FactoryBean itself will generally
	 * be provided by the owning BeanFactory; usually, it has to be
	 * defined as singleton there.
	 * <p><b>NOTE:</b> This method returning {@code false} does not
	 * necessarily indicate that returned objects are independent instances.
	 * An implementation of the extended {@link SmartFactoryBean} interface
	 * may explicitly indicate independent instances through its
	 * {@link SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
	 * implementations which do not implement this extended interface are
	 * simply assumed to always return independent instances if the
	 * {@code isSingleton()} implementation returns {@code false}.
	 * <p>The default implementation returns {@code true}, since a
	 * {@code FactoryBean} typically manages a singleton instance.
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	//判断该bean是否是单例bean
	default boolean isSingleton() {
		return true;
	}

}
