package com.marvin.test.self.tags.resolver;

import com.marvin.test.self.tags.parser.MarvinBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class MarvinNameSpaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		//注册自定义标签的bean的xml标签解析器
		registerBeanDefinitionParser("marvin",new MarvinBeanDefinitionParser());
	}
}
