package com.yesee.gov.website.model.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_tb_voucher_detail", catalog = "yesee")
public class VTbVoucherDetail implements java.io.Serializable {

	@Id
	@Column(name = "voucher_no")
	private String voucherNo;
	
	@Id
	@Column(name = "hedge_no")
	private String hedgeNo;
	
	@Column(name = "detail_item")
	private String detailItem;
	
	@Column(name = "project")
	private String project;
	
	@Column(name = "amount")
	private String amount;
	
	@Column(name = "directions")
	private String directions;
	
	@Column(name = "credit_date")
	private String creditDate;
	
	@Column(name = "predict_date")
	private String predictDate;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "applicant")
	private String applicant;
	
	@Column(name = "customer")
	private String customer;
	
	@Column(name = "cus_tax_id")
	private String cusTaxId;
	
	@Column(name = "company")
	private String company;	
	
	public VTbVoucherDetail() {
		super();
	}
	
	
	public VTbVoucherDetail(String voucherNo, String hedgeNo, String detailItem, String project, String amount,
			String directions, String creditDate, String predictDate, String status, String applicant, String customer,
			String cusTaxId, String company) {
		super();
		this.voucherNo = voucherNo;
		this.hedgeNo = hedgeNo;
		this.detailItem = detailItem;
		this.project = project;
		this.amount = amount;
		this.directions = directions;
		this.creditDate = creditDate;
		this.predictDate = predictDate;
		this.status = status;
		this.applicant = applicant;
		this.customer = customer;
		this.cusTaxId = cusTaxId;
		this.company = company;
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
	public String getDetailItem() {
		return detailItem;
	}
	public void setDetailItem(String detailItem) {
		this.detailItem = detailItem;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	
}
