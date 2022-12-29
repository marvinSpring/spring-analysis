/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.self.tag;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Conditional({MarvinConditional.class})
@Configuration
public class MarvinBeanConfiguration {

	@Bean(name = "xiaoMa")
	public MarvinBean marvinBean(){
		return new MarvinBean("Ma");
	}

	@Bean(name = "little")
	public MarvinBean marvinBean2(){
		return new MarvinBean("La");
	}
}
