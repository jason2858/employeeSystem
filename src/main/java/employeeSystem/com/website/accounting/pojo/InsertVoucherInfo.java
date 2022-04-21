package employeeSystem.com.website.accounting.pojo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class InsertVoucherInfo {

	@JsonProperty("voucher_name")
	private String voucherName;

	@JsonProperty("company")
	private String company;

	@JsonProperty("customer")
	private String customer;

	@JsonProperty("cus_tax_id")
	private String cusTaxId;

	@JsonProperty("head_item")
	private String headItem;

	@JsonProperty("hedge_no_h")
	private String hedgeNoH;

	@JsonProperty("amount_total")
	private Integer amountTotal;

	@JsonProperty("applicant")
	private String applicant;

	@JsonProperty("credit_date")
	private String creditDate;

	@JsonProperty("predict_date")
	private String predictDate;

	@JsonProperty("common")
	private String common;

	@JsonProperty("tax_id_type")
	private String taxIdType;

	@JsonProperty("directions")
	private String directions;

	private List<Map<String, String>> detail;

	public String getVoucherName() {
		return voucherName;
	}

	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getHeadItem() {
		return headItem;
	}

	public void setHeadItem(String headItem) {
		this.headItem = headItem;
	}

	public String getHedgeNoH() {
		return hedgeNoH;
	}

	public void setHedgeNoH(String hedgeNoH) {
		this.hedgeNoH = hedgeNoH;
	}

	public Integer getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(Integer amountTotal) {
		this.amountTotal = amountTotal;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
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

	public String getCommon() {
		return common;
	}

	public void setCommon(String common) {
		this.common = common;
	}

	public String getTaxIdType() {
		return taxIdType;
	}

	public void setTaxIdType(String taxIdType) {
		this.taxIdType = taxIdType;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public List<Map<String, String>> getDetail() {
		return detail;
	}

	public void setDetail(List<Map<String, String>> detail) {
		this.detail = detail;
	}

}