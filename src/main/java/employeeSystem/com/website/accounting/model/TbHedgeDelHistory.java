package com.yesee.gov.website.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_hedge_del_history", catalog = "yesee")
public class TbHedgeDelHistory implements java.io.Serializable {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, length = 20)
	private Integer id;
	
	@Column(name = "hedge_no",  nullable = false, length = 20)
	private String hedgeNo;

	@Column(name = "balance", nullable = false)
	private Integer balance;

	@Column(name = "create_user", nullable = false, length = 20)
	private String createUser;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	public TbHedgeDelHistory() {
		super();
	}

	public TbHedgeDelHistory(Integer id, String hedgeNo, Integer balance, String createUser, Date createDate) {
		super();
		this.id = id;
		this.hedgeNo = hedgeNo;
		this.balance = balance;
		this.createUser = createUser;
		this.createDate = createDate;
	}

	public TbHedgeDelHistory(String hedgeNo, Integer balance, String createUser, Date createDate) {
		super();
		this.hedgeNo = hedgeNo;
		this.balance = balance;
		this.createUser = createUser;
		this.createDate = createDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHedgeNo() {
		return hedgeNo;
	}

	public void setHedgeNo(String hedgeNo) {
		this.hedgeNo = hedgeNo;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
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
