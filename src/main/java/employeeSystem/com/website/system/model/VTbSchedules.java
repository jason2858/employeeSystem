package com.yesee.gov.website.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "v_tb_schedules", catalog = "yesee")
public class VTbSchedules implements java.io.Serializable {
	
	private String user;
	private Float overtimeTotal;
	private Float resttimeTotal;
	private Float availableTime;
	
	public VTbSchedules() {
	}

	public VTbSchedules(String user, Float overtimeTotal, Float resttimeTotal, Float availableTime) {
		
		super();
		this.user = user;
		this.overtimeTotal = overtimeTotal;
		this.resttimeTotal = resttimeTotal;
		this.availableTime = availableTime;
		
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "user", unique = true, nullable = false)
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Column(name = "overtime_total")
	public Float getOvertimeTotal() {
		return overtimeTotal;
	}

	public void setOvertimeTotal(Float overtimeTotal) {
		this.overtimeTotal = overtimeTotal;
	}

	@Column(name = "rest_total")
	public Float getResttimeTotal() {
		return resttimeTotal;
	}

	public void setResttimeTotal(Float resttimeTotal) {
		this.resttimeTotal = resttimeTotal;
	}

	@Column(name = "available_time")
	public Float getAvailableTime() {
		return availableTime;
	}

	public void setAvailableTime(Float availableTime) {
		this.availableTime = availableTime;
	}
	
	


}