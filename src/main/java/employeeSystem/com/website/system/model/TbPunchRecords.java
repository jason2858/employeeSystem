package com.yesee.gov.website.model;
import static javax.persistence.GenerationType.IDENTITY;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * TbPunchRecords generated by hbm2java
 */
@Entity
@Table(name = "tb_punch_records", catalog = "yesee")
public class TbPunchRecords implements java.io.Serializable {

	private Long id;
	private TbEmployees tbEmployeesByUser;
	private TbEmployees tbEmployeesBySigner;
	private Date punchTime;
	private String type;
	private String note;
	private Date createdAt;
	private Date updatedAt;
	private String status;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private Date signedAt;
	private String positionInfo;

	public TbPunchRecords() {
	}

	public TbPunchRecords(TbEmployees tbEmployeesByUser, Date punchTime, String type, String status) {
		this.tbEmployeesByUser = tbEmployeesByUser;
		this.punchTime = punchTime;
		this.type = type;
		this.status = status;
	}

	public TbPunchRecords(TbEmployees tbEmployeesByUser, TbEmployees tbEmployeesBySigner, Date punchTime, String type,
			String note, Date createdAt, Date updatedAt, String status, BigDecimal latitude, BigDecimal longitude,
			Date signedAt, String positionInfo) {
		this.tbEmployeesByUser = tbEmployeesByUser;
		this.tbEmployeesBySigner = tbEmployeesBySigner;
		this.punchTime = punchTime;
		this.type = type;
		this.note = note;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
		this.latitude = latitude;
		this.longitude = longitude;
		this.signedAt = signedAt;
		this.positionInfo = positionInfo;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user", nullable = false)
	public TbEmployees getTbEmployeesByUser() {
		return this.tbEmployeesByUser;
	}

	public void setTbEmployeesByUser(TbEmployees user) {
		this.tbEmployeesByUser = user;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "signer")
	public TbEmployees getTbEmployeesBySigner() {
		return this.tbEmployeesBySigner;
	}

	public void setTbEmployeesBySigner(TbEmployees signer) {
		this.tbEmployeesBySigner = signer;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "punch_time", nullable = false, length = 19)
	public Date getPunchTime() {
		return this.punchTime;
	}

	public void setPunchTime(Date punchTime) {
		this.punchTime = punchTime;
	}

	@Column(name = "type", nullable = false, length = 9)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "note", length = 200)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", length = 19)
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", length = 19)
	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "status", nullable = false, length = 9)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "latitude", precision = 15, scale = 10)
	public BigDecimal getLatitude() {
		return this.latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitude", precision = 15, scale = 10)
	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "signed_at", length = 19)
	public Date getSignedAt() {
		return this.signedAt;
	}

	public void setSignedAt(Date signedAt) {
		this.signedAt = signedAt;
	}

	@Column(name = "positionInfo", length = 1)
	public String getPositionInfo() {
		return this.positionInfo;
	}

	public void setPositionInfo(String positionInfo) {
		this.positionInfo = positionInfo;
	}

}
