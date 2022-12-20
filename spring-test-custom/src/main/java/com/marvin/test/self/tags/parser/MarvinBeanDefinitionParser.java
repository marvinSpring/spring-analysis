package com.marvin.test.self.tags.parser;

import com.marvin.test.self.tags.entity.Marvin;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 对marvin类型的自定义标签进行解析
 */
public class MarvinBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	//返回自定义标签对应类
	@Override
	protected Class<?> getBeanClass(Element element) {
		return Marvin.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		//获取自定义标签具备的属性
		String username = element.getAttribute("username");
		String password = element.getAttribute("password");
		String age = element.getAttribute("age");
		//对属性进行校验及赋值
		if (StringUtils.hasText(username)){
			builder.addPropertyValue("username",username);
		}
		if (StringUtils.hasText(password)){
			builder.addPropertyValue("password",password);
		}
		if (StringUtils.hasText(age)){
			if (age.contains("-")){
				try {
					throw new Exception(String.format("年龄 [%s] 不能为复数",age));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			int actualAge = -1;
			try {
				actualAge = Integer.parseInt(age);
			} catch (NumberFormatException e) {
				try {
					throw new Exception(String.format("年龄 [%s] 必须为数字",age));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			builder.addPropertyValue("age",actualAge);
		}
	}
}
