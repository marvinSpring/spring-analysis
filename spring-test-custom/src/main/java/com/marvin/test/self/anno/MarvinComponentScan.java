/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.self.anno;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan("com.marvin.test.self.tag")
public class MarvinComponentScan {
	public MarvinComponentScan() {
	}

	private MarvinInnerClass marvinInnerClass;

	public MarvinInnerClass getMarvinInnerClass() {
		return marvinInnerClass;
	}

	public void setMarvinInnerClass(MarvinInnerClass marvinInnerClass) {
		this.marvinInnerClass = marvinInnerClass;
	}

	@Configuration
	@ComponentScan("com.marvin.test.self.tag")
	static class MarvinInnerClass{

		public MarvinInnerClass() {
		}


	}
}
