/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 对象的简单接口，这些对象是{@link InputStream}的源。
 *
 * <p>这是Spring更广泛的{@link Resource}接口的基本接口。
 *
 * <p>对于一次性流，{@link InputStreamResource}可以用于任何给定的{@code InputStream}。
 * Spring的{@link ByteArrayResource}或任何基于文件的{@code Resource}实现都可以用作具体实例，
 * 从而允许其多次读取基础内容流。
 * 例如，这使该接口可用作邮件附件的抽象内容源。
 *
 * @author Juergen Hoeller
 * @since 20.01.2004
 * @see java.io.InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource {

	/**
	 * 返回{@link InputStream}作为基础资源的内容。
	 * <p>预计每个调用都会创建一个新的流。
	 * <p>当考虑使用JavaMail之类的API时，此要求特别重要，
	 * 当创建邮件附件时，该API必须能够多次读取流。
	 * 对于这种用例，<i>要求<i>每个{@code getInputStream()}调用都返回一个新的流。
	 * @return the input stream for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content stream could not be opened
	 */
	InputStream getInputStream() throws IOException;

}
