/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.marvin.test.tree;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

	public static void main(String[] args) {
		Menu menu = new Menu();
		menu.setId(1L);
		menu.setName("root");
		menu.setPid(0L);
		Menu menu2 = new Menu();
		menu2.setId(2L);
		menu2.setName("level 1");
		menu2.setPid(1L);
		Menu menu3 = new Menu();
		menu3.setId(3L);
		menu3.setName("levle 1 temp");
		menu3.setPid(1L);
		Menu menu4 = new Menu();
		menu4.setId(4L);
		menu4.setName("root");
		menu4.setPid(2L);
		Menu menu5 = new Menu();
		menu5.setId(5L);
		menu5.setName("level 2");
		menu5.setPid(4L);
		Menu menu6 = new Menu();
		menu6.setId(6L);
		menu6.setName("level 3");
		menu6.setPid(5L);
		List<Menu> menuList = Arrays.asList(menu, menu2, menu3, menu4, menu5, menu6);
		List<Menu> treeMenus = tree(menuList);
		System.out.println(treeMenus);
	}

	private static List<Menu> tree(List<Menu> menuList) {
		return generate(menuList,menuList.stream().filter(x->x.getPid()!=0L).collect(Collectors.groupingBy(Menu::getPid)));
	}

	private static List<Menu> generate(List<Menu> all, Map<Long,List<Menu>> subMap) {
		for (Menu menu : all) {
			if (CollectionUtils.isEmpty(menu.getChilds())){
				menu.setChilds(subMap.get(menu.getId()));
				return generate(all,subMap);
			}
		}
		return all;
	}
}
