package com.yesee.gov.website.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yesee.gov.website.model.TbGroup;

/**
 * TbSignRole generated by hbm2java
 */
@Entity
@Table(name = "tb_sign_role", catalog = "yesee")
public class TbSignRole implements java.io.Serializable {

	@Id
	@Column(name = "role_id", unique = true, nullable = false, length = 2)
	private String roleId;

//	@Column(name = "sign_lv", nullable = false, length = 2)
//	private Integer signLv;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role", referencedColumnName = "name", nullable = false, insertable = true, updatable = true)
	private TbGroup role;

	@Column(name = "create_user", nullable = false, length = 25)
	private String createUser;

	@Column(name = "create_date", nullable = false, length = 10)
	private Date createDate;

	public TbSignRole() {
	}

	public TbSignRole(String roleId, TbGroup role, String createUser, Date createDate) {
		super();
		this.roleId = roleId;
		this.role = role;
		this.createUser = createUser;
		this.createDate = createDate;
	}

//	public TbSignRole(String roleId, Integer signLv, TbGroup role, String createUser, Date createDate) {
//		super();
//		this.roleId = roleId;
//		this.signLv = signLv;
//		this.role = role;
//		this.createUser = createUser;
//		this.createDate = createDate;
//	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

//	public Integer getSignLv() {
//		return signLv;
//	}
//
//	public void setSignLv(Integer signLv) {
//		this.signLv = signLv;
//	}

	public TbGroup getRole() {
		return role;
	}

	public void setRole(TbGroup tbGroup) {
		this.role = tbGroup;
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