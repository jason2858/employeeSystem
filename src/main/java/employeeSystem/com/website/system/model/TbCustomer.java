package com.yesee.gov.website.model;
// default package
// Generated 2019/3/21 �W�� 10:44:58 by Hibernate Tools 4.3.5.Final
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * TbCustomer generated by hbm2java
 */
@Entity
@Table(name = "tb_customer", catalog = "yesee")
public class TbCustomer implements java.io.Serializable {
	private Integer id;
	private String name;
	private Integer parentId;
	private String type;
	private String info;
	private String ein;
	private String contactPerson;
	private String contactPhone;
	private String contactEmail;
	private String creator;
	private String status;
	private Date updatedAt;
	public TbCustomer() {
	}
	public TbCustomer(String name, String type, String status) {
		this.name = name;
		this.type = type;
		this.status = status;
	}
	public TbCustomer(String name, Integer parentId, String type, String info, String ein, String contactPerson,
			String contactPhone, String contactEmail, String creator, String status, Date updatedAt) {
		this.name = name;
		this.parentId = parentId;
		this.type = type;
		this.info = info;
		this.ein = ein;
		this.contactPerson = contactPerson;
		this.contactPhone = contactPhone;
		this.contactEmail = contactEmail;
		this.creator = creator;
		this.status = status;
		this.updatedAt = updatedAt;
	}
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "name", nullable = true, length = 256)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "parent_id")
	public Integer getParentId() {
		return this.parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	@Column(name = "type", nullable = false, length = 10)
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Column(name = "info", length = 65535)
	public String getInfo() {
		return this.info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	@Column(name = "ein", length = 64)
	public String getEin() {
		return this.ein;
	}
	public void setEin(String ein) {
		this.ein = ein;
	}
	@Column(name = "contact_person", length = 32)
	public String getContactPerson() {
		return this.contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	@Column(name = "contact_phone", length = 32)
	public String getContactPhone() {
		return this.contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	@Column(name = "contact_email", length = 64)
	public String getContactEmail() {
		return this.contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	@Column(name = "creator", length = 100)
	public String getCreator() {
		return this.creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	@Column(name = "status", nullable = false, length = 12)
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at", length = 19)
	public Date getUpdatedAt() {
		return this.updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}