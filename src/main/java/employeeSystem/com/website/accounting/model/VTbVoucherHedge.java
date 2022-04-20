package com.yesee.gov.website.model.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_tb_voucher_hedge", catalog = "yesee")
public class VTbVoucherHedge {

	@Id
	@Column(name = "hedge_no", unique = true, nullable = false, length = 20)
	private String hedgeNo;

	@Column(name = "balance", nullable = false)
	private Integer balance;

	@Column(name = "project_id", length = 11)
	private String projectId;

	@Column(name = "hedge_item", length = 20)
	private String hedgeItem;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Column(name = "directions", length = 200)
	private String directions;

	@Column(name = "credit_date", length = 10)
	private String creditDate;

	@Column(name = "predict_date", length = 10)
	private String predictDate;

	@Column(name = "company", length = 20)
	private String company;

	public VTbVoucherHedge() {
	}

	public VTbVoucherHedge(String hedgeNo, Integer balance, String projectId, String hedgeItem, Integer amount,
			String directions, String creditDate, String predictDate, String company) {
		this.hedgeNo = hedgeNo;
		this.balance = balance;
		this.projectId = projectId;
		this.hedgeItem = hedgeItem;
		this.amount = amount;
		this.directions = directions;
		this.creditDate = creditDate;
		this.predictDate = predictDate;
		this.company = company;
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getHedgeItem() {
		return hedgeItem;
	}

	public void setHedgeItem(String hedgeItem) {
		this.hedgeItem = hedgeItem;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public String getCreditDate() {
		return creditDate;
	}

	public void setCreditDate(String creditDate) {
		this.creditDate = creditDate;
	}

	public String getPredictDate() {
		return predictDate;
	}

	public void setPredictDate(String predictDate) {
		this.predictDate = predictDate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
