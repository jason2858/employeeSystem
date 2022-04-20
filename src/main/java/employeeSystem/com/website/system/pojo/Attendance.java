package com.yesee.gov.website.pojo;

import org.springframework.stereotype.Component;

@Component
public class Attendance {
	
	private String type;
	
	private String startTime;
	
	private String createdAt;
	
	private String status;
	
	private String note;
	
	private String signer;
	
	private String id;
	
	private String endTime;
	
	private String user;
	
	private String annualLeaveTimes;
	
	private String deputy;
	
	private String formNo;
	
	private String signAt;
	
	private String reason;
	
	
	public String getSignAt() {
		return signAt;
	}

	public void setSignAt(String signAt) {
		this.signAt = signAt;
		
	}public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getFormNo() {
		return formNo;
	}

	public void setFormNo(String formNo) {
		this.formNo = formNo;
	}
	
	public String getDeputy() {
		return deputy;
	}

	public void setDeputy(String deputy) {
		this.deputy = deputy;
	}
	
	public String getAnnualLeaveTimes() {
		return annualLeaveTimes;
	}

	public void setAnnualLeaveTimes(String annualLeaveTimes) {
		this.annualLeaveTimes = annualLeaveTimes;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getSigner() {
		return signer;
	}

	public void setSigner(String signer) {
		this.signer = signer;
	}

}
