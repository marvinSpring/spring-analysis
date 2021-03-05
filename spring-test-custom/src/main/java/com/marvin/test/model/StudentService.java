package com.marvin.test.model;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

//@Service
public class StudentService {

	public List<Employee> findAll() throws InterruptedException {
		Employee employee = new Employee(20L,"18309296327(已删除)",null,null,3,null, "assasa", LocalDateTime.now());
		Employee employee2 = new Employee(20L,"18309296327(已删除)",null,null,3,null, "assasa",LocalDateTime.now());
		Thread.sleep(100);
		return Arrays.asList(employee, employee2);
	}
}
