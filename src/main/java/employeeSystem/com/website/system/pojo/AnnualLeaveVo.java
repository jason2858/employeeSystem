package com.yesee.gov.website.pojo;

import org.springframework.stereotype.Component;

//for setting & passing data to fullcalendar
@Component
public class AnnualLeaveVo {
	
	private String empName;
	
	private String accountName;
	
	private String entitledHours;
	
	private String year;
	
	private String skdHours;
	
	private String pastLeave;
	
	private String dep;

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEntitledHours() {
		return entitledHours;
	}

	public void setEntitledHours(String entitledHours) {
		this.entitledHours = entitledHours;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSkdHours() {
		return skdHours;
	}

	public void setSkdHours(String skdHours) {
		this.skdHours = skdHours;
	}

	public String getPastLeave() {
		return pastLeave;
	}

	public void setPastLeave(String pastLeave) {
		this.pastLeave = pastLeave;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


}
