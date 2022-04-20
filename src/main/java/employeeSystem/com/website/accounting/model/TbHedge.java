package com.yesee.gov.website.model.accounting;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tb_hedge", catalog = "yesee")
public class TbHedge implements java.io.Serializable {

	@Id
	@Column(name = "hedge_no", unique = true, nullable = false, length = 20)
	private String hedgeNo;

	@Column(name = "balance", nullable = false)
	private Integer balance;

	@Column(name = "create_user", nullable = false, length = 25)
	private String createUser;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "hedge_no")
	private List<TbVoucherHedge> voucherHedgeList;

	public TbHedge() {
	}

	public TbHedge(String hedgeNo, Integer balance, String createUser, Date createDate) {
		this.hedgeNo = hedgeNo;
		this.balance = balance;
		this.createUser = createUser;
		this.createDate = createDate;
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

	public List<TbVoucherHedge> getVoucherHedgeList() {
		return voucherHedgeList;
	}

	public void setVoucherHedgeList(List<TbVoucherHedge> voucherHedgeList) {
		this.voucherHedgeList = voucherHedgeList;
	}

}
