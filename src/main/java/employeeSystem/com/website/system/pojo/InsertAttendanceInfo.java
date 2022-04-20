package com.yesee.gov.website.pojo;

import org.springframework.stereotype.Component;

//for getting new attendance info from front end
@Component
public class InsertAttendanceInfo {
	
	private String type;
	private String note;
	private String startTime;
	private String endTime;
	private String annualLeaveTimes;
	private String deputy;
	
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

}
