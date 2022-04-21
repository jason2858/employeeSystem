package employeeSystem.com.website.accounting.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_tb_report_balance", catalog = "yesee")
public class VTbReportBalance implements java.io.Serializable {

	@Id
	@Column(name = "hedge_no", length = 20)
	private String hedgeNo;

	@Column(name = "balance")
	private Integer balance;

	@Column(name = "voucher_no", length = 11)
	private String voucherNo;

	@Column(name = "company", length = 20)
	private String company;

	@Column(name = "item", length = 4)
	private String item;

	@Column(name = "item_name", length = 20)
	private String itemName;

	@Column(name = "credit_date", length = 10)
	private String creditDate;

	@Column(name = "predict_date", length = 10)
	private String predictDate;

	@Column(name = "customer", length = 20)
	private String customer;

	@Column(name = "cus_tax_id", length = 8)
	private String cusTaxId;

	@Column(name = "directions", length = 200)
	private String directions;

	public VTbReportBalance() {

	}

	public VTbReportBalance(String hedgeNo, Integer balance, String voucherNo, String company, String item,
			String itemName, String creditDate, String predictDate, String customer, String cusTaxId,
			String directions) {
		super();
		this.hedgeNo = hedgeNo;
		this.balance = balance;
		this.voucherNo = voucherNo;
		this.company = company;
		this.item = item;
		this.itemName = itemName;
		this.creditDate = creditDate;
		this.predictDate = predictDate;
		this.customer = customer;
		this.cusTaxId = cusTaxId;
		this.directions = directions;
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

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

}