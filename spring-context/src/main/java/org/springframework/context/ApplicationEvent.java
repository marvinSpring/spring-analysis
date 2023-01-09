/*
 * Copyright 2002-2018 the original author or authors.
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

import java.util.EventObject;

/**
 * 要由所有应用程序事件扩展的类。摘要，
 * 因为直接发布通用事件没有意义。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class ApplicationEvent extends EventObject {

	/** 使用Spring 1.2的serialVersionUID实现互操作性. */
	private static final long serialVersionUID = 7099057708183571937L;

	/** 事件发生时的系统时间. */
	private final long timestamp;


	/**
	 * 创建一个新的 ApplicationEvent.
	 * @param source 最初发生事件的对象（从不为 {@code null}）
	 */
	public ApplicationEvent(Object source) {
		super(source);
		this.timestamp = System.currentTimeMillis();
	}


	/**
	 * 返回事件发生时系统时间（以毫秒为单位）。
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}

}
