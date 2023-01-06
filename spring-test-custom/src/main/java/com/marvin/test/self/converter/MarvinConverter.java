/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.self.converter;

import com.marvin.test.self.tags.entity.Marvin;
import org.springframework.core.convert.converter.Converter;

public class MarvinConverter implements Converter<String, Marvin> {

	@Override
	public Marvin convert(String source) {
		String[] strings = source.split("_");
		Marvin marvin = new Marvin();
		marvin.setAge(Integer.parseInt(strings[0]));;
		marvin.setUsername(strings[1]);
		marvin.setPassword(strings[2]);
		return marvin;
	}

}
