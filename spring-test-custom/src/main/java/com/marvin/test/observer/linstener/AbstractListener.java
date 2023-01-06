/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.observer.linstener;

import com.marvin.test.observer.event.AbstractEvent;

/**
 * listen to @param type's event
 * @param <T>
 */
public abstract class AbstractListener<T extends AbstractEvent> {

	/**
	 * listen event and to do something
	 * @param event event
	 */
	public abstract void doSomething(AbstractEvent event);

}
