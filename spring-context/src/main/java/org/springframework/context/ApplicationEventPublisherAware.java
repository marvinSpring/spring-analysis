/*
 * Copyright 2002-2011 the original author or authors.
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

import org.springframework.beans.factory.Aware;

/**
 * 希望由希望在其运行的ApplicationEventPublisher（通常是ApplicationContext）
 * 得到通知的任何对象实现的接口。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.1
 * @see ApplicationContextAware
 */
public interface ApplicationEventPublisherAware extends Aware {

	/**
	 * 设置此对象在其中运行的ApplicationEventPublisher。
	 * <p>在填充正常的bean属性之后但在诸如InitializingBean的afterPropertiesSet或自定义init方法之类的init回调之前调用。
	 * 在ApplicationContextAware的setApplicationContext之前调用。 
	 * @param applicationEventPublisher此对象要使用的事件发布者
	 */
	void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher);

}
