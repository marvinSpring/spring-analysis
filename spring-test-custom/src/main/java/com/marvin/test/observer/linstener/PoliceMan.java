/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.observer.linstener;

import com.marvin.test.observer.event.AbstractEvent;
import com.marvin.test.observer.event.MessageEvent;

/**
 * lazy to write comment,it's like given @see
 * @see Peoples
 */
public class PoliceMan extends AbstractListener<AbstractEvent> {

	private PoliceMan(String name) {
		this.name = name;
	}

	private final String name;

	public static Builder builder = new Builder();

	static public class Builder{

		private String name;

		public Builder name(String name){
			this.name = name;
			return this;
		}

		public PoliceMan buildPoliceMan(){
			return new PoliceMan(name);
		}

	}

	@Override
	public void doSomething(AbstractEvent event) {
		System.out.printf("%s, police man:{"+name+"} take up gun start run ,%n",event.getSource());
	}
}
