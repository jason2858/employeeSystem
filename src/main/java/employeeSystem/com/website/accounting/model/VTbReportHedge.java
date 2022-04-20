package com.yesee.gov.website.model.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_tb_report_hedge", catalog = "yesee")
public class VTbReportHedge implements java.io.Serializable {

	@Id
	@Column(name = "voucher_no", length = 12)
	private String voucherNo;
	
	@Id
	@Column(name = "hedge_no", length = 20)
	private String hedgeNo;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "project")
	private String project;

	@Column(name = "item", length = 4)
	private String item;
	
	@Column(name = "item_name", length = 20)
	private String itemName;
	
	@Column(name = "amount")
	private Integer amount;
	
	@Column(name = "company", length = 20)
	private String company;
	
	@Column(name = "applicant", length = 20)
	private String applicant;

	@Column(name = "customer", length = 20)
	private String customer;

	@Column(name = "cus_tax_id", length = 8)
	private String cusTaxId;

	@Column(name = "credit_date")
	private String creditDate;

	@Column(name = "directions", length = 200)
	private String directions;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "balance")
	private Integer balance;

	public VTbReportHedge() {
		super();
	}

	public VTbReportHedge(String voucherNo, String hedgeNo, String type, String project, String item, String itemName,
			Integer amount, String company, String applicant, String customer, String cusTaxId, String creditDate,
			String directions, String status, Integer balance) {
		super();
		this.voucherNo = voucherNo;
		this.hedgeNo = hedgeNo;
		this.type = type;
		this.project = project;
		this.item = item;
		this.itemName = itemName;
		this.amount = amount;
		this.company = company;
		this.applicant = applicant;
		this.customer = customer;
		this.cusTaxId = cusTaxId;
		this.creditDate = creditDate;
		this.directions = directions;
		this.status = status;
		this.balance = balance;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getHedgeNo() {
		return hedgeNo;
	}

	public void setHedgeNo(String hedgeNo) {
		this.hedgeNo = hedgeNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCusTaxId() {
		return cusTaxId;
	}

	public void setCusTaxId(String cusTaxId) {
		this.cusTaxId = cusTaxId;
	}

	public String getCreditDate() {
		return creditDate;
	}

	public void setCreditDate(String creditDate) {
		this.creditDate = creditDate;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	
	
}
