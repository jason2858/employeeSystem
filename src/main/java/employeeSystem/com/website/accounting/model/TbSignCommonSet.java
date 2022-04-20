package com.yesee.gov.website.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yesee.gov.website.model.TbEmployees;

@Entity
@Table(name = "tb_sign_common_set", catalog = "yesee")
public class TbSignCommonSet implements java.io.Serializable {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sign_name",referencedColumnName = "sign_name", nullable = false, insertable = true, updatable = true)
	private TbSignCommon signName;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", referencedColumnName = "role_id",nullable = false, insertable = true, updatable = true)
	private TbSignRole roleId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sign_user",referencedColumnName = "username", nullable = false, insertable = true, updatable = true)
	private TbEmployees signUser;

	@Column(name = "create_user",  length = 25)
	private String createUser;

	@Column(name = "create_date", length = 10)
	private Date createDate;

	public TbSignCommonSet() {
	}


	public TbSignCommonSet(TbSignCommon signName, TbSignRole roleId, TbEmployees signUser, String createUser,
			Date createDate) {
		super();
		this.signName = signName;
		this.roleId = roleId;
		this.signUser = signUser;
		this.createUser = createUser;
		this.createDate = createDate;
	}


	public TbSignCommon getSignName() {
		return signName;
	}

	public void setSignName(TbSignCommon tbSignCommon) {
		this.signName = tbSignCommon;
	}

	public TbSignRole getRoleId() {
		return roleId;
	}

	public void setRoleId(TbSignRole tbSignRole) {
		this.roleId = tbSignRole;
	}

	public TbEmployees getSignUser() {
		return signUser;
	}

	public void setSignUser(TbEmployees tbEmployees) {
		this.signUser = tbEmployees;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
