package com.marvin.test.self.edtior.support;


import com.marvin.test.self.edtior.entity.Marvin;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class MarvinPropertyEditorSupport extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			String[] strings = text.split("_");
			Marvin marvin = new Marvin();
			try {
				marvin.setFirstUsername(strings[0]);
				marvin.setSecondUsername(strings[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException(String.format("字符 [%s] 不符合 marvin 自定义编译器的解析规范",text));
			}
			setValue(marvin);
			return;
		}
		throw new IllegalArgumentException(String.format("未找到 字符 [%s]",text));
	}
}
