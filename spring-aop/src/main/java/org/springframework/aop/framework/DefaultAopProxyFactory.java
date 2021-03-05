/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;

import org.springframework.aop.SpringProxy;

/**
 * 默认的AopProxyFactory实现，创建CGLIB代理或JDK动态代理。
 *
 * 如果给定的AdvisedSupport实例满足以下条件之一，则创建CGLIB代理：
	 · 设置了optimize标志
	 · 设置了proxyTargetClass标志
	 · 没有指定代理接口
 *
 * 通常，指定proxyTargetClass来强制执行CGLIB代理，或者指定一个或多个接口来使用JDK动态代理。
 * 补充：可以设置{@link org.springframework.context.annotation.EnableAspectJAutoProxy#proxyTargetClass}
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see AdvisedSupport#setOptimize
 * @see AdvisedSupport#setProxyTargetClass
 * @see AdvisedSupport#setInterfaces
 */
@SuppressWarnings("serial")
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {

	@Override
	//创建Aop代理
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		//判断配置是否有效，配置中有代理类信息，判断有没有用户自己定义的解析接口
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			//如果是接口，或者Proxy的类，返回默认的JDK动态代理对象
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			//CGLIB方式
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			//JDK动态代理
			return new JdkDynamicAopProxy(config);
		}
	}

	/**
	 * 确定提供的{@link AdvisedSupport}是否仅指定了{@link org.springframework.aop.SpringProxy}接口（或完全没有指定代理接口）。
	 */
	private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {//判断目标类的父类是否是接口
		Class<?>[] ifcs = config.getProxiedInterfaces();
		return (ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0])));
	}

}
