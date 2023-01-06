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

package org.springframework.core;

/**
 * {@link Ordered} 接口的扩展，表示<em>优先级<em>排序：{@code PriorityOrdered}
 * 对象始终在<em>纯<em> {@link Ordered} 对象之前应用，无论其顺序值如何。
 *
 * <p>对一组 {@code 有序} 对象进行排序时，{@code PriorityOrdered} 对象和普通 {@code 有序} 对象
 * 实际上<em><em>被视为两个单独的子集，其中一组 {@code PriorityOrdered} 对象
 * 位于<em>一组纯<em> {@code 有序} 对象之前，并在这些子集中应用相对排序。
 *
 * <p>这主要是一个特殊用途的接口，在框架本身中用于对象，其中<em>首先识别优先级对象特别重要<em>，
 * 甚至可能没有获得其余对象。
 * 一个典型的例子：Spring {@link org.springframework.context.ApplicationContext} 中的优先级后处理器。
 *
 * <p>注意：{@code PriorityOrdered} 后处理器 bean 在一个特殊阶段初始化，
 * 先于其他后处理器 bean。这微妙地影响了它们的自动注入行为：
 * 它们只会针对不需要急切初始化进行类型匹配的 bean 自动注入。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 * @see org.springframework.beans.factory.config.PropertyOverrideConfigurer
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
public interface PriorityOrdered extends Ordered {
}
