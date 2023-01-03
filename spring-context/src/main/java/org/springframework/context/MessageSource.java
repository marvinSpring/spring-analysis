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

package org.springframework.context;

import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 *
 * 当前接口提供了用于处理消息的策略，包含了信息的国际化和参数信息的替换。
 *
 * <p>Spring 为生产提供了两个开箱即用的实现：
 *  	<ul>
 *  		<li>
 *  		    {@link org.springframework.context.support.ResourceBundleMessageSource}:
 *  		    建立在标准{@link java.util.ResourceBundle}之上，共享其局限性。
 *  		<li>
 *  		     {@link org.springframework.context.support.ReloadableResourceBundleMessageSource}:
 *  		     高度可配置，特别是在重新加载消息定义方面。
 *  	</ul>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.context.support.ResourceBundleMessageSource
 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
 */
public interface MessageSource {

	/**
	 * 解析{code}对应的信息进行返回，如果对应的code不能被解析，则返回defaultMessage，
	 * code：需要被解析的编码，对应资源文件中一个属性的名称
	 * args：需要用来替换的code，对应信息中包含参数的内容
	 * defaultMessage：当没有从对应资源文件中获取到code对应的信息的时候需要返回的一个默认值
	 * locale：对应的Locale对象，用于执行查找的区域设置，比如查找中国、英国、意大利等国家对应的语言
	 *
	 * 尝试解决该消息。如果未找到消息，则返回默认消息。
	 * @param code，例如“calculator.noRateSet”。
	 * 建议 MessageSource 用户将消息名称基于限定的类或包名称，
	 * 以避免潜在的冲突并确保最大程度的清晰度。
	 * @param args，这些参数将为消息中的参数填充（参数在消息中看起来像“{0}”、“{1，date}”、“{2，time}”），如果没有，则为 {@code null}
	 *
	 * @param defaultMessage a default message to return if the lookup fails
	 * @param locale the locale in which to do the lookup
	 * @return the resolved message if the lookup was successful, otherwise
	 * the default message passed as a parameter (which may be {@code null})
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see java.text.MessageFormat
	 */
	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

	/**
	 * 	 * 解析{code}对应的信息进行返回
	 * 	 * code：需要被解析的编码，对应资源文件中一个属性的名称
	 * 	 * args：需要用来替换的code，对应信息中包含参数的内容
	 * 	 * locale：对应的Locale对象，用于执行查找的区域设置，比如查找中国、英国、意大利等国家对应的语言
	 *
	 * Try to resolve the message. Treat as an error if the message can't be found.
	 * @param code the message code to look up, e.g. 'calculator.noRateSet'.
	 * MessageSource users are encouraged to base message names on qualified class
	 * or package names, avoiding potential conflicts and ensuring maximum clarity.
	 * @param args an array of arguments that will be filled in for params within
	 * the message (params look like "{0}", "{1,date}", "{2,time}" within a message),
	 * or {@code null} if none
	 * @param locale the locale in which to do the lookup
	 * @return the resolved message (never {@code null})
	 * @throws NoSuchMessageException if no corresponding message was found
	 * @see #getMessage(MessageSourceResolvable, Locale)
	 * @see java.text.MessageFormat
	 */
	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;

	/**
	 * 通过MessageSourceResolvable来对不同Locale这个进行消息解析
	 *
	 * Try to resolve the message using all the attributes contained within the
	 * {@code MessageSourceResolvable} argument that was passed in.
	 * <p>NOTE: We must throw a {@code NoSuchMessageException} on this method
	 * since at the time of calling this method we aren't able to determine if the
	 * {@code defaultMessage} property of the resolvable is {@code null} or not.
	 * @param resolvable the value object storing attributes required to resolve a message
	 * (may include a default message)
	 * @param locale the locale in which to do the lookup
	 * @return the resolved message (never {@code null} since even a
	 * {@code MessageSourceResolvable}-provided default message needs to be non-null)
	 * @throws NoSuchMessageException if no corresponding message was found
	 * (and no default message was provided by the {@code MessageSourceResolvable})
	 * @see MessageSourceResolvable#getCodes()
	 * @see MessageSourceResolvable#getArguments()
	 * @see MessageSourceResolvable#getDefaultMessage()
	 * @see java.text.MessageFormat
	 */
	String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException;

}
