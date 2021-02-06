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

/**
 * 定义用于启停生命周期控制的方法的通用接口。
 * 典型的例子是控制————异步处理。
 * <b>注意：此接口并不暗示特定的自动启动语义。
 * 考虑为此目的使用{@link SmartLifecycle}。<b>
 *
 * <p>可以由Component（通常是在Spring上下文中定义的Spring bean）和Container
 * （通常是Spring {@link ApplicationContext}本身）实现。
 * Container会将启停信号传播到每个容器中应用的所有组件，例如在运行时停止重启的情况。
 *
 * <p>可用于直接调用或通过JMX进行管理操作。
 * 在后一种情况下，通常使用{@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler}
 * 定义{@link org.springframework.jmx.export.MBeanExporter}，从而将活动控制的组件的可见性限制为生命周期界面。
 *
 * <p>请注意，当前的{@code Lifecycle}接口仅在<b>顶级单例 bean <b>上受支持。
 * 在任何其他组件上，{@code Lifecycle}接口将保持未被检测到并因此被忽略。
 * 另外，请注意，扩展的{@link SmartLifecycle}接口提供了与应用程序上下文的启动和关闭阶段的复杂集成。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * @see org.springframework.jms.listener.AbstractMessageListenerContainer
 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

	/**
	 * 启动此组件。
	 * <p>如果组件已经在运行，则不应引发异常。
	 * <p>对于容器，这会将启动信号传播到所有适用的组件。
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 *通常以同步方式停止此组件，以使该组件在返回此方法后完全停止。
	 * 当需要异步停止行为时，请考虑实现{@link SmartLifecycle}及其{@code stop（Runnable）}变体。
	 * p>请注意，此停止通知不能保证在销毁之前出现：在常规关闭时，{@code Lifecycle} bean在传播一般销毁回调之前将首先接收到停止通知。
	 * 但是，在上下文生命周期中进行热刷新或中止刷新尝试时，将调用给定bean的destroy方法，而无需事先考虑停止信号。
	 * <p>如果组件未运行（尚未启动），则不应引发异常。 <p>对于容器，这会将停止信号传播到所有适用的组件。
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * 检查此组件当前是否正在运行。 <p>在使用容器的情况下，
	 * 仅当当前应用的所有<i>所有<i>组件正在运行时，它才会返回{@code true}。
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
