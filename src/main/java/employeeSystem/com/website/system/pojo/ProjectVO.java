package com.yesee.gov.website.pojo;

import java.util.Date;

public class ProjectVO {
	
	private String id;
	private String projectName;
	private String typeDropdown;
	private String projectPm;
	private String siDropdown;
	private String endUserDropdown;
	private String devStatus;
	private String updatedAt;
	private String estimateHour;
	private String actualHour;

	public String getProjectPm() {
		return projectPm;
	}

	public void setProjectPm(String projectPm) {
		this.projectPm = projectPm;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTypeDropdown() {
		return typeDropdown;
	}

	public void setTypeDropdown(String typeDropdown) {
		this.typeDropdown = typeDropdown;
	}

	public String getSiDropdown() {
		return siDropdown;
	}

	public void setSiDropdown(String siDropdown) {
		this.siDropdown = siDropdown;
	}

	public String getEndUserDropdown() {
		return endUserDropdown;
	}

	public void setEndUserDropdown(String endUserDropdown) {
		this.endUserDropdown = endUserDropdown;
	}

	public String getDevStatus() {
		return devStatus;
	}

	public void setDevStatus(String devStatus) {
		this.devStatus = devStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String getEstimateHour() {
		return estimateHour;
	}

	public void setEstimateHour(String estimateHour) {
		this.estimateHour = estimateHour;
	}
	
	public String getActualHour() {
		return actualHour;
	}

	public void setActualHour(String actualHour) {
		this.actualHour = actualHour;
	}
}
