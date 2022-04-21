package employeeSystem.com.website.accounting.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_accounting_balance", catalog = "yesee")
public class TbAccountingBalance implements java.io.Serializable {

	@EmbeddedId
	private TbAccountingBalancePK tbAccountingBalancePK;

	@Column(name = "balance", nullable = false)
	private Integer balance;

	@Column(name = "create_user", nullable = false, length = 20)
	private String createUser;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	public TbAccountingBalance() {
	}

	public TbAccountingBalance(TbAccountingBalancePK tbAccountingBalancePK, Integer balance, String createUser,
			Date createDate) {
		this.tbAccountingBalancePK = tbAccountingBalancePK;
		this.balance = balance;
		this.createUser = createUser;
		this.createDate = createDate;
	}

	public TbAccountingBalancePK getTbAccountingBalancePK() {
		return tbAccountingBalancePK;
	}

	public void setTbAccountingBalancePK(TbAccountingBalancePK tbAccountingBalancePK) {
		this.tbAccountingBalancePK = tbAccountingBalancePK;
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
