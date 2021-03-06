package employeeSystem.com.website.accounting.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import employeeSystem.com.website.system.model.TbEmployees;

/**
 * TbVoucherSign generated by hbm2java
 */
@Entity
@Table(name = "tb_voucher_sign", catalog = "yesee")
public class TbVoucherSign implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_no", unique = true, nullable = false, length = 15)
	private Integer seqNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voucher_no", nullable = false, insertable = true, updatable = true)
	private TbVoucherHead voucherNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sign_user", nullable = false, insertable = true, updatable = true)
	private TbEmployees signUser;

	@Column(name = "v_code", nullable = false, length = 16)
	private String vCode;

	@Column(name = "sign_user_check", length = 10)
	private String signUserCheck;

	@Column(name = "sign_date")
	private Date signDate;

	@Column(name = "sign_type", length = 10)
	private String signType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false, insertable = true, updatable = true)
	private TbSignRole roleId;

	@Column(name = "reason", length = 10)
	private String reason;

	@Column(name = "create_user", nullable = false, length = 10)
	private String createUser;

	@Column(name = "create_date", nullable = false)
	private Date createDate;

	public TbVoucherSign() {
	}

	public TbVoucherSign(Integer seqNo, TbVoucherHead voucherNo, TbEmployees signUser, String vCode,
			String signUserCheck, Date signDate, String signType, TbSignRole roleId, String reason, String createUser,
			Date createDate) {
		super();
		this.seqNo = seqNo;
		this.voucherNo = voucherNo;
		this.signUser = signUser;
		this.vCode = vCode;
		this.signUserCheck = signUserCheck;
		this.signDate = signDate;
		this.signType = signType;
		this.roleId = roleId;
		this.reason = reason;
		this.createUser = createUser;
		this.createDate = createDate;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public TbVoucherHead getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(TbVoucherHead voucherNo) {
		this.voucherNo = voucherNo;
	}

	public TbEmployees getSignUser() {
		return signUser;
	}

	public void setSignUser(TbEmployees signUser) {
		this.signUser = signUser;
	}

	public String getvCode() {
		return vCode;
	}

	public void setvCode(String vCode) {
		this.vCode = vCode;
	}

	public String getSignUserCheck() {
		return signUserCheck;
	}

	public void setSignUserCheck(String signUserCheck) {
		this.signUserCheck = signUserCheck;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public TbSignRole getRoleId() {
		return roleId;
	}

	public void setRoleId(TbSignRole roleId) {
		this.roleId = roleId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
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