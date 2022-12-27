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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	private PostProcessorRegistrationDelegate() {
	}


	// 如果有BeanDefinitionRegistryPostProcessors接口的实现类，就调用BeanDefinitionRegistryPostProcessors接口的实现类
	public static void invokeBeanFactoryPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		//无论什么情况都会优先执行BeanDefinitionRegistryPostProcessor
		//这个集合中存的是已经执行过的BFPP bean,防止重复执行
		Set<String> processedBeans = new HashSet<>();

		//-----------
		// 判断bean工厂是否支持bean定义信息的增删改查,如果当前bean工厂都不拥有bean定义信息的增删改查的能力的话,那去执行BDRPP也没有意义

		//ps:(默认创建的DefaultListableBeanFactory是实现了这个接口的)，
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			//此处的BeanDefinitionRegistryPostProcessor是BeanFactoryPostProcessor的子集、BeanFactoryPostProcessor主要增强操作的对象是bean工厂，
			// 而BeanDefinitionRegistryPostProcessor主要增强操作的对象的Bean的定义信息
			//存放BFPP的有规则,有等级的集合
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			//存放BDRPP的集合
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			//得到所有的BeanFactoryPostProcessor  遍历
			/* 在创建context时，通过调用#addBeanFactoryPostProcessor()方法，将实现了BeanFactoryPostProcessor的类实例化（通过new的方式），
			   然后放入到集合中，在这里进行调用，如果用户不自己实现BeanFactoryPostProcessor(一般没有默认的实现类),则默认不会调用*/
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				//如果是BDRPP那么去执行BDRPP的逻辑
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					BeanDefinitionRegistryPostProcessor registryProcessor =
							(BeanDefinitionRegistryPostProcessor) postProcessor;
					//调用实现BeanDefinitionRegistryPostProcessor接口的类的postProcessBeanDefinitionRegistry方法，对BeanDefinition进行增删改查
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					//添加到registryProcessor,用于在后续执行BFPP系列的方法
					registryProcessors.add(registryProcessor);
				}
				else {
					//否则,就是没有BDRPP能力而有BFPP能力的类,将这些类添加到regularPostProcessor中,也用于在后续执行BFPP系列的方法
					regularPostProcessors.add(postProcessor);
				}
			}

			// Do not initialize FactoryBeans here: We need to leave all regular beans
			// uninitialized to let the bean factory post-processors apply to them!
			// 分开 拥有PriorityOrdered、Ordered的BDRPP 和剩余没有排序能力的 BeanDefinitionRegistryPostProcessor
			//用于本次要执行的BDRPP
			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// 根据类型从容器中获取到所有的BeanDefinitionRegistryPostProcessor
			/* 首先，从beanFactory中找所有有BeanDefinitionRegistryPostProcessor能力的类*/
			String[] postProcessorNames =
					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			//  第一步，先执行有PriorityOrdered排序能力的BeanDefinitionRegistryPostProcessor
			for (String ppName : postProcessorNames) {
				/* 判断类上是否存在实现了PriorityOrdered排序接口，如果存在，则调用getBean将其实例化后放入currentRegistryProcessors中*/
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {//判断当前这个每次的BDRPP是否有PriorityOrdered的能力
					//获取有PriorityOrdered能力的BDRPP实例然后添加到 当前马上要增删改查的后置处理器 中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//将该名称标记在 执行后的后置处理器集合 中，表示该BDRPP对象已经马上要被执行过了,防止底下的人重复执行
					processedBeans.add(ppName);
				}
			}
			//对当前的这些有PriorityOrdered能力的BDRPP根据其优先级去排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 将排序后的BDRPP放入另外一个registryProcessors集合中，用于最后执行BFPP的能力方法
			registryProcessors.addAll(currentRegistryProcessors);
			//遍历执行BDRPP的能力方法
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//将当前要执行的currentRegistryProcessors集合清空，以便后面重复利用这个集合
			currentRegistryProcessors.clear();

			//第二步，再执行拥有Ordered能力的BeanDefinitionRegistryPostProcessor
			//这里再次去根据 类型 寻找的原因是在上面代码执行的过程中,在invokeBeanDefinitionRegistryPostProcessor方法中可能又新增了其他的BDRPP
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				//过滤拥有Ordered能力但是未被上面执行过的bean----->也就是拥有Ordered能力但是大概率不拥有PriorityOrdered能力,
				// 如果在上面的invokeBeanDefinitionRegistryPostProcessor中才新增的PriorityOrdered能力者将在下面一并执行
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					//获取只有Ordered能力的BDRPP实例然后添加到 当前马上要增删改查的后置处理器 中
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					//继续添加这些只有Ordered能力的BDRPP的bean到 已经马上要处理完成的后置处理器集合 中
					processedBeans.add(ppName);
				}
			}
			//这次按照Ordered能力去排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			//添加当前这些Ordered能力者到 增删改查增强器集合 中,用于最后使用他们的BFPP能力
			registryProcessors.addAll(currentRegistryProcessors);
			//遍历执行他们拥有的BDRPP的能力
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			//继续清理当前的 增删改查增强器集合 以便后面继续循环利用
			currentRegistryProcessors.clear();

			// 第三步 执行不拥有任何优先级或者是顺序能力的 Bean定义信息增删改查器
			//这个reiterate标记代表是否要继续迭代这个while
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				//找到剩下没有PriorityOrdered也没有Ordered能力的BDRPP
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				//调用 其他没有Ordered或者PriorityOrdered能力，只是有BeanDefinitionRegistryPostProcessor的能力的方法
				for (String ppName : postProcessorNames) {
					//当然,上面这个根据类型找还是会把有PriorityOrdered能力者和Ordered能力者找出来,把他们skip掉,
					// 也就是用上面的这个执行过的增强器集合去筛选剩下的无排序能力者的BDRPP
					if (!processedBeans.contains(ppName)) {
						//将这些没有能力的再次的加入到 当前马上要执行的Bean增删改查增强器集合 中
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						//然后把这些 马上要被处理完成的继续标记到 即将处理完成的集合中
						processedBeans.add(ppName);
						//如果还能找到要处理的这些无能力者,那么就继续再次看看能不能找到更多的无能力者
						// @see invokeBeanDefinitionRegistryPostProcessors,这里面可能被子类扩展出新的 无能力者,这样的话就又需要循环一次了
						reiterate = true;
					}
				}
				//按照优先级对他们进行排序,如果有的话,因为这里还是有可能被上面的invokeBeanDefinitionRegistryPostProcessors方法派生出有排序能力的BDRPP们
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				//继续将 当前的这些Bean的增删改查增强器 加入 增删改查增强器集合中 以便后面统一他们的使用BFPP的能力
				registryProcessors.addAll(currentRegistryProcessors);
				//这里大概里只执行没有任何排序能力者的BDRPP能力
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				//继续清理 当前的增删改查增强器集合 这里不是没意义的,因为有可能有下次循环
				// @see reiterate
				// 哪怕没有下次循环了,也清空掉比较合理,于程序设计来说这里清理是属于有头有尾,于GC来讲,也可以尽快将其加入老年代区域进行清理
				currentRegistryProcessors.clear();
			}

			//使用上面层层获取到的BFPP能力
			/* 调用所有实现BeanDefinitionRegistryPostProcessor中的postProcessBeanFactory方法*/
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
			//当前bean工厂支持 增删改查器的 使用外部入参的BFPP能力
			/* 调用在addBeanFactoryPostProcessor()中已经创建好的对象的postProcessBeanFactory方法*/
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}

		else {
			//当前bean工厂不支持 增删改查增强器的 使用外部入参的只有BFPP能力者的BFPP能力
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}

		//-------------
		//到此为止、入参中外部的BFPP和容器内的BDRPP能力者已经全部处理完成了,接下来要处理容器内的BFPP

		// 再执行内部的BFPP的能力者的BFPP能力
		// 1）、根据类型获取所有的BFPP者
		// 和执行BDRPP的能力者的逻辑大致一样
		String[] postProcessorNames =
				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

		//把BFPP分类 分为:
		//1.有优先级能力的实力派兼有背景BFPP
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		//2.只有Ordered能力的有背景的BFPP
		List<String> orderedPostProcessorNames = new ArrayList<>();
		//3.最后是啥几把能力没有 没有实力也没有背景的 小瘪三级别的BFPP
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();

		//循环这些BFPP,过滤分配到上面的分类集合中
		for (String ppName : postProcessorNames) {
			//这些BFPP已经被上面的BDRPP能力者领域执行过就跳过,防止重复执行
			if (processedBeans.contains(ppName)) {
			}
			//判断是否是有实力的PriorityOrdered的BFPP能力者
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			//判断是否是有背景的Ordered的BFPP能力者
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			//判断是不是啥也不是的小瘪三 BFPP能力者
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 2）、先对拥有优先级排序的BFPP能力者进行排序
		/* 分组调用实现类(实现了PriorityOrdered和BeanFactoryPostProcessors的实现类)，和上边的逻辑一样*/
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		//再根据排序后的BFPP能力者进行BFPP能力的使用
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// 3）、其次对拥有普通Ordered的BFPP能力者进行排序调用
		//需要注意的是,上下文其实不确定这会是不是又被invokeBeanFactoryPostProcessors这个方法派生了新的BFPP出来,所以再往这个 orderedPostProcessors 集合里塞刚找到的新鲜的BFPP
		/* 接下来，调用实现Ordered的BeanFactoryPostProcessors。*/
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// 4）、最后对这些普通的小瘪三BFPP进行能力使用,那他们也没有啥排序能力干脆就不排了
		// 但是和第三步同样需要注意是不是上一步的 invokeBeanFactoryPostProcessors 这个方法又新增啥东西进去了,不知道,所以梅开二度,我再去找一遍呗
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		//清除缓存（postProcessor修改的数据）,mergedBeanDefinitions、allBeanNamesByType、singletonBeanNameByType
		//上面在执行后置处理器的过程中,派生的子类扩展可能把这些修改掉了,比如替换符中的占位符$改成个%,类似这样的

		//ps:有头有尾,这里一般开发者想不到哦,因为这个头不是最开始开的,
		//而是子类派发的扩展的方法执行出来的缓存,这个东西要开发者提前把spring的BFPP后置增强器能做什么,生命周期等提前想好
		beanFactory.clearMetadataCache();
	}

	public static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {

		//1.获取所有的 BeanPostProcessor;后置处理器都默认可以通过PriorityOrdered、Ordered接口来执行优先级
		/* 找到所有实现了BeanPostProcessor的类,将类名放入到postProcessorNames数组中*/
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		//检查器，检查所有的BeanPostProcessor
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

		// 将实现了PriorityOrdered，Ordered和其余优先级的BeanPostProcessor之间分开。
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		//将所有实现了BeanPostProcessor接口的实现类，实例化后放入BeanFactory容器中，注意：此时已经实例化好，但不从这里进行方法调用，而是在实例化普通bean时进行前置和后置调用
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 2.注册实现PriorityOrdered接口的BeanPostProcessors实现类
		/*
			先注册PriorityOrdered优先级接口的BeanPostProcessor；
            把每一个BeanPostProcessor；添加到BeanFactory中
            beanFactory.addBeanPostProcessor(postProcessor);
         */
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 把对应的 BeanPostProcessor 对象注册到 BeanFactory 中，BeanFactory 中有一个 list 容器接收。
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// 3.再注册实现Ordered接口的BeanPostProcessors实现类
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// 4.最后注册没有实现任何优先级接口的实现类
		// 注册正常(普通的没有实现和继承任何其它奇奇怪怪的接口)的BeanPostProcessors实现类
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// 5.最终注册MergedBeanDefinitionPostProcessor；
		/* 最后，注册实现了MergedBeanDefinitionPostProcessor的BeanPostProcessors实现类*/
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// 不重要
		// 6.注册一个ApplicationListenerDetector；判断bean是否创建完成的监听器
		/* 在Bean创建完成后ApplicationListenerDetector.postProcessAfterInitialization()中检查是否是ApplicationListener 类型，
		   如果是applicationContext.addApplicationListener((ApplicationListener<?>) bean);从而添加到容器中 */
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		//如果集合只有一个或者一个都没有,没必要排序了,走吧
		if (postProcessors.size() <=1 ){
			return;
		}
		Comparator<Object> comparatorToUse = null;
		//判断是否是可罗列的Bean工厂
		if (beanFactory instanceof DefaultListableBeanFactory) {
			//获取依赖的比较器
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			//如果没设置依赖的比较器,则使用一个默认的比较器
			comparatorToUse = OrderComparator.INSTANCE;
		}
		//使用 比较器 对 后置增强器 进行排序
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
