package com.marvin.test.self.edtior.registrar;

import com.marvin.test.self.edtior.entity.Marvin;
import com.marvin.test.self.edtior.support.MarvinPropertyEditorSupport;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

public class MarvinPropertyEditorRegistrar implements PropertyEditorRegistrar {


	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Marvin.class, new MarvinPropertyEditorSupport());
	}
}
