package com.marvin.test.controller;

import com.marvin.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

	@Autowired
	private TestService testService;

	public void test(){
		testService.test();
	}
}
