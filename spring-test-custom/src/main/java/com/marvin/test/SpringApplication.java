package com.marvin.test;

import com.marvin.test.controller.TestController;
import com.marvin.test.model.*;
import com.marvin.test.self.bfpp.MarvinBean;
import com.marvin.test.self.bfpp.MarvinExtendsBeanDefinitionRegistryPostProcessor;
import com.marvin.test.self.edtior.entity.User;
import com.marvin.test.self.tags.entity.Marvin;
import com.marvin.test.service.TestService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.objenesis.instantiator.util.ClassUtils;

import java.time.LocalDateTime;

/**
 * @author Marvin
 */

public class SpringApplication {



}
