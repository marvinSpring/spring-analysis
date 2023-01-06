/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.observer.publish;

import com.marvin.test.observer.event.AbstractEvent;
import com.marvin.test.observer.linstener.AbstractListener;

import java.util.HashSet;
import java.util.Set;

/**
 * mock spring publisher
 * @param <T>
 */
public abstract class AbstractPublisher<T extends AbstractEvent> {

	//need monitor listeners
	public Set<AbstractListener<AbstractEvent>> listeners = new HashSet<>();

	//publish event to listener
	public void publish(AbstractEvent event){
		before();
		listeners.forEach(x->x.doSomething(event));
	}

	//the method is hook ,for mock aop or bean lifecycle
	public abstract void before();

	//add listener , there should use java.reflect to do but i'm so lazy,just so see
	public void addListener(AbstractListener<AbstractEvent> listener){
		listeners.add(listener);
	}

	//remove listener , there should use java.reflect to do but I'm so lazy,just so see
	public void removeListener(AbstractListener<AbstractEvent> listener){
		listeners.remove(listener);
	}

}
