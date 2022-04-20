package com.yesee.gov.website.model;
//default package
//Generated 2018/7/19 �U�� 03:30:01 by Hibernate Tools 4.3.1.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
* TbGroupFunction generated by hbm2java
*/
@Entity
@Table(name = "tb_scheduled_leave", catalog = "yesee")
public class TbScheduledLeave implements java.io.Serializable {

	private TbScheduledLeaveId id;
	private TbEmployees tbEmployees;
	private String skdHours;

	public TbScheduledLeave() {
	}

	public TbScheduledLeave(TbScheduledLeaveId id, TbEmployees tbEmployees) {
		this.id = id;
		this.tbEmployees = tbEmployees;
	}

	public TbScheduledLeave(TbScheduledLeaveId id, TbEmployees tbEmployees, String skdHours) {
		this.id = id;
		this.tbEmployees = tbEmployees;
		this.skdHours = skdHours;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "empName", column = @Column(name = "emp_name", nullable = false, length = 100)),
			@AttributeOverride(name = "skdDate", column = @Column(name = "skd_date", nullable = false, length = 256)) })
	public TbScheduledLeaveId getId() {
		return this.id;
	}

	public void setId(TbScheduledLeaveId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_name", nullable = false, insertable = false, updatable = false)
	public TbEmployees getTbEmployees() {
		return this.tbEmployees;
	}

	public void setTbEmployees(TbEmployees tbEmployees) {
		this.tbEmployees = tbEmployees;
	}

	@Column(name = "skd_hours", length = 256)
	public String getSkdHours() {
		return this.skdHours;
	}

	public void setSkdHours(String skdHours) {
		this.skdHours = skdHours;
	}

}