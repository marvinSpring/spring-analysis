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

import java.util.List;

/**
 * mock listener
 */
public class Peoples extends AbstractListener<AbstractEvent> {

	//self members field
	private final List<String> nameList;

	//builder for singleton bean
	public static Builder builder = new Builder();

	//private constructor for singleton bean
	private Peoples(List<String> nameList) {
		this.nameList = nameList;
	}

	/**
	 * builder for build {@link Peoples}
	 */
	static public class Builder{

		//temp field
		private List<String> nameList;

		//mock setter
		public Builder nameList(List<String> peopleNames){
			nameList = peopleNames;
			return this;
		}

		//build
		public Peoples buildPeoples(){
			return new Peoples(nameList);
		}
	}

	@Override
	public void doSomething(AbstractEvent event) {
		for (String name : nameList) {
			System.out.printf("people :{"+name+"} eat %s melon,continue eat melon%n",event.getSource());
		}
	}
}
