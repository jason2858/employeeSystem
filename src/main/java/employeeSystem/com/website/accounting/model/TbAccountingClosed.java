package employeeSystem.com.website.accounting.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_accounting_closed", catalog = "yesee")
public class TbAccountingClosed implements java.io.Serializable {

	@EmbeddedId
	private TbAccountingClosedPK tbAccountingClosedPK;

	@Column(name = "status", nullable = false, length = 1)
	private String status;

	@Column(name = "create_user", nullable = false, length = 20)
	private String createUser;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	public TbAccountingClosed() {
	}

	public TbAccountingClosed(TbAccountingClosedPK tbAccountingClosedPK, String status, String createUser,
			Date createDate) {
		this.tbAccountingClosedPK = tbAccountingClosedPK;
		this.status = status;
		this.createUser = createUser;
		this.createDate = createDate;
	}

	public TbAccountingClosedPK getTbAccountingClosedPK() {
		return tbAccountingClosedPK;
	}

	public void setTbAccountingClosedPK(TbAccountingClosedPK tbAccountingClosedPK) {
		this.tbAccountingClosedPK = tbAccountingClosedPK;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
