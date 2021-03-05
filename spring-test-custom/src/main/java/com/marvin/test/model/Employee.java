package com.marvin.test.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Employee {

    private Long id;
    private String phone;
    private String clientId;
    private String companyId;
    private Integer status;
    private String trueName;

	public Employee(Long id, String phone, String clientId, String companyId, Integer status, String trueName, String password, LocalDateTime createTime) {
		this.id = id;
		this.phone = phone;
		this.clientId = clientId;
		this.companyId = companyId;
		this.status = status;
		this.trueName = trueName;
		this.password = password;
		this.createTime = createTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + id +
				", phone='" + phone + '\'' +
				", clientId='" + clientId + '\'' +
				", companyId='" + companyId + '\'' +
				", status=" + status +
				", trueName='" + trueName + '\'' +
				", password='" + password + '\'' +
				", createTime=" + createTime +
				'}';
	}

	private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
