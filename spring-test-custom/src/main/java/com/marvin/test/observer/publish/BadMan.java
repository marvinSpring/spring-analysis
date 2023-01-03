/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.observer.publish;

import com.marvin.test.observer.event.AbstractEvent;
import com.marvin.test.observer.event.MessageEvent;
import com.marvin.test.observer.linstener.Peoples;
import com.marvin.test.observer.linstener.PoliceMan;

import java.util.Arrays;

/**
 * actually one mock need publish's class
 */
public class BadMan extends AbstractPublisher<AbstractEvent> {

	/**
	 * end-user self-definition's method
	 */
	public void run(){
		System.out.println("i ready to run");

		publish(new MessageEvent("bad man running"));
	}

	@Override
	public void before() {
		addListener(PoliceMan.builder.name("little ma").buildPoliceMan());
		addListener(Peoples.builder.nameList(Arrays.asList("marvin","teng.ma")).buildPeoples());
	}

}
