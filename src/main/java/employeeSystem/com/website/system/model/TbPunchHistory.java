package com.yesee.gov.website.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "tb_punch_history", catalog = "yesee")
public class TbPunchHistory implements java.io.Serializable {

	
	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp", referencedColumnName = "username", nullable = false, unique = true, insertable = true, updatable = true)
	private TbEmployees emp;
	
	@Id
	@Column(name = "punch_date",nullable = false,length=8)
	private String punchDate;

	@Id
	@Column(name = "type",nullable = false,length=1)
	private String type;
	
	@Column(name = "punch_check",nullable = false,length=1)
	private String punchCheck;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	public TbPunchHistory() {
		super();
	}

	public TbPunchHistory(TbEmployees emp, String punchDate, String type, String punchCheck, Date createDate,
			Date updateDate) {
		super();
		this.emp = emp;
		this.punchDate = punchDate;
		this.type = type;
		this.punchCheck = punchCheck;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	public TbEmployees getEmp() {
		return emp;
	}

	public void setEmp(TbEmployees emp) {
		this.emp = emp;
	}

	public String getPunchDate() {
		return punchDate;
	}

	public void setPunchDate(String punchDate) {
		this.punchDate = punchDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPunchCheck() {
		return punchCheck;
	}

	public void setPunchCheck(String punchCheck) {
		this.punchCheck = punchCheck;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	
	
	
	
}