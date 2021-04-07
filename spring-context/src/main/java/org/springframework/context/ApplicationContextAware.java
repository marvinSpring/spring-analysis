/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * 希望由希望在其运行{@link ApplicationContext}的时候收到通知的任何对象实现的接口。
 * <p>例如，当对象需要访问一组协作bean时，实现此接口是有意义的。
 * 请注意，仅出于bean查找目的，通过bean引用进行配置比实现此接口更可取。
 * <p>如果对象需要访问文件资源（例如，要调用{@code getResource}，要
 * 发布应用程序事件或需要访问MessageSource），则也可以实现此接口。
 * 但是，在这种特定情况下，最好实现更具体的{@link ResourceLoaderAware}，
 * {@link ApplicationEventPublisherAware }或{@link MessageSourceAware}接口。
 * <p>请注意，文件资源依赖项也可以公开为
 * {@link org.springframework.core.io.Resource}类型的bean属性，由bean工厂通过自动类型转换的字符串填充。
 * 这样就无需为了访问特定文件资源而实现任何回调接口。
 * <p> {@link org.springframework.context.support.ApplicationObjectSupport}是应用程序对象的便捷基类，实现了此接口。
 * <p>For a list of all bean lifecycle methods, see the
 * {@link org.springframework.beans.factory.BeanFactory BeanFactory javadocs}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

	/**
	 * 设置该对象在其中运行的ApplicationContext。
	 * 通常，此调用将用于初始化该对象。
	 * <p>在填充正常的bean属性之后但在初始化回调之前调用，
	 * 例如{@link org.springframework.beans.factory.InitializingBeanafterPropertiesSet（）}
	 * 或自定义的初始化方法。
	 * 如果适用，在{@link ResourceLoaderAwaresetResourceLoader}，{@link ApplicationEventPublisherAwaresetApplicationEventPublisher}和{@link MessageSourceAware}之后调用。
	 * @param applicationContext the ApplicationContext object to be used by this object
	 * @throws ApplicationContextException in case of context initialization errors
	 * @throws BeansException if thrown by application context methods
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
