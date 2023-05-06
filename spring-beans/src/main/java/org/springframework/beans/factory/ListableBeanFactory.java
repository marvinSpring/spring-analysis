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

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * {@link BeanFactory}接口的扩展将由可以枚举其所有bean实例的bean工厂实现，
 * 而不是按照客户端的请求按名称一一尝试进行bean查找。
 * 预加载其所有bean定义的BeanFactory实现（例如基于XML的工厂）可以实现此接口。
 *
 * <p>如果这是{@link HierarchicalBeanFactory}，则返回值<i>不<i>会考虑任何BeanFactory层次结构，
 * 但仅与当前工厂中定义的bean有关。也可以使用{@link BeanFactoryUtils}帮助器类来考虑祖先工厂中的bean。
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton} method, with the exception of
 * {@code getBeanNamesOfType} and {@code getBeansOfType} which will check
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * <p><b>注意:<b>除了{@code getBeanDefinitionCount}和{@code containsBeanDefinition}之外，这个接口中的方法不是为频繁调用而设计的。实现可能很慢。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
//可罗列Bean的Bean工厂，是一个List的形式
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * 检查此bean工厂是否包含具有给定名称的bean定义。
	 * <p>不考虑该工厂可能参与的任何层次结构，并忽略通过bean定义以外的其他方式注册的任何单例bean。
	 * @param beanName 要查找的bean的名称
	 * @return 如果此bean工厂包含具有给定名称的bean定义
	 * @see #containsBean
	 */
	//是否包含BeanDefinition
	boolean containsBeanDefinition(String beanName);

	/**
	 * 返回在工厂中定义的bean的数量。
	 * <p>不考虑此工厂可能参与的任何层次结构，
	 * 并忽略通过bean定义以外的其他方式注册的任何单例bean。
	 * @return 在工厂中定义的bean数量
	 */
	//获取Bean Definition的数量
	int getBeanDefinitionCount();

	/**
	 * 返回此工厂中定义的所有bean的名称。
	 * <p>不考虑此工厂可能参与的任何层次结构，
	 * 并忽略通过bean定义以外的其他方式注册的任何单例bean。
	 * @return 此工厂中定义的所有bean的名称，
	 * 如果没有定义则为空数组
	 */
	//获取BeanDefinition的名称数组
	String[] getBeanDefinitionNames();

	/**
	 * 返回与给定类型(包括子类)匹配的bean名称，
	 * 根据bean定义或{@code getObjectType}(对于FactoryBeans)的值判断。
	 * <p><b>注意:此方法仅自查顶级bean。</b> 它也不检查可能匹配指定类型的嵌套bean。
	 * <p>考虑由FactoryBeans创建的对象，
	 * 这意味着FactoryBeans将被初始化。
	 * 如果FactoryBean创建的对象不匹配，原始FactoryBean本身将根据类型进行匹配。
	 * <p>不考虑此工厂可能参与的任何层次结构。
	 * 使用BeanFactoryUtils的{@code beanNamesForTypeIncludingAncestors}也可以在祖先工厂中包含bean。
	 * <p>注意:
	 * 不忽略通过bean定义以外的其他方式注册的单例bean。
	 * <p>{@code getBeanNamesForType}的这个版本匹配所有类型的bean，
	 * 无论是单例bean、原型bean还是factoryBean。在大多数实现中，结果将与{@code getBeanNamesForType(type, true, true)}相同。
	 * <p>此方法返回的Bean名称应该总是尽可能按照在后端配置中定义的顺序返回Bean名称。
	 * @param type 要匹配的泛型类或接口
	 * @return 匹配给定对象类型(包括子类)的bean(或由FactoryBeans创建的对象)的名称，如果没有则为空数组
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of {@code getBeanNamesForType} matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeanNamesForType(type, true, true)}.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of {@code getObjectType}
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beanNamesForTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * @param type 要匹配的类或接口，或 {@code null} 表示所有 Bean 名称
	 * @param includeNonSingletons 是否也包含原型或范围 bean 或仅包含单例（也适用于 FactoryBeans）
	 * @param allowEagerInit 是否初始化由FactoryBeans创建的懒加载的单例和对象以进行类型检查。
	 * 请注意，需要急切地初始化 FactoryBeans 以确定它们的类型：
	 * 所以请注意，为此标志传入 “true” 将初始化 FactoryBeans 和 “factory-bean” 引用。
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of getBeansOfType matches all kinds of beans, be it
	 * singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for {@code getBeansOfType(type, true, true)}.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	//根据类型获取Bean
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * {@code getObjectType} in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' {@code beansOfTypeIncludingAncestors}
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or {@code null} for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * Find all names of beans which are annotated with the supplied {@link Annotation}
	 * type, without creating corresponding bean instances yet.
	 * <p>Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * @param annotationType the type of annotation to look for
	 * @return the names of all matching beans
	 * @since 4.0
	 * @see #findAnnotationOnBean
	 */
	//根据注解获取Bean名称
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * Find all beans which are annotated with the supplied {@link Annotation} type,
	 * returning a Map of bean names with corresponding bean instances.
	 * <p>Note that this method considers objects created by FactoryBeans, which means
	 * that FactoryBeans will get initialized in order to determine their object type.
	 * @param annotationType the type of annotation to look for
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 3.0
	 * @see #findAnnotationOnBean
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * Find an {@link Annotation} of {@code annotationType} on the specified bean,
	 * traversing its interfaces and super classes if no annotation can be found on
	 * the given class itself.
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the type of annotation to look for
	 * @return the annotation of the given type if found, or {@code null} otherwise
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 3.0
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 */
	//找到某个Bean上的注解
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
