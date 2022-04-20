package com.yesee.gov.website.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "v_tb_punch_history", catalog = "yesee")
public class VTbPunchHistory implements java.io.Serializable {
	
	
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private int id;
	@Id
	@Column(name = "account", length = 20)
	private String account;

	@Column(name = "name")
	private String name;
	
	@Id
	@Column(name = "punch_date")
	private String punchDate;
	
	@Id
	@Column(name = "schedules")
	private String schedules;
	
	@Column(name = "in_status")
	private String inStatus;
	
	@Column(name = "out_status")
	private String outStatus;
	
	@Id
	@Column(name = "schedules_time")
	private String schedulesTime;

	public VTbPunchHistory() {
		super();
	}

	public VTbPunchHistory(int id, String account, String name, String punchDate, String schedules, String inStatus,
			String outStatus,String schedulesTime) {
		super();
		this.account = account;
		this.name = name;
		this.punchDate = punchDate;
		this.schedules = schedules;
		this.inStatus = inStatus;
		this.outStatus = outStatus;
		this.schedulesTime = schedulesTime;
	}


	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPunchDate() {
		return punchDate;
	}

	public void setPunchDate(String punchDate) {
		this.punchDate = punchDate;
	}

	public String getSchedules() {
		return schedules;
	}

	public void setSchedules(String schedules) {
		this.schedules = schedules;
	}

	public String getInStatus() {
		return inStatus;
	}

	public void setInStatus(String inStatus) {
		this.inStatus = inStatus;
	}

	public String getOutStatus() {
		return outStatus;
	}
	
	public void setOutStatus(String outStatus) {
		this.outStatus = outStatus;
	}

	public String getSchedulesTime() {
		return schedulesTime;
	}
	
	public void setSchedulesTime(String schedulesTime) {
		this.schedulesTime = schedulesTime;
	}
}


	