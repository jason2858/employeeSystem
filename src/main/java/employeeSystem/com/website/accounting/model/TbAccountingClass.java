package com.yesee.gov.website.model.accounting;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * TbGroupFunction generated by hbm2java
 */
@Entity
@Table(name = "tb_accounting_class", catalog = "yesee")
public class TbAccountingClass implements java.io.Serializable {

	@Id
	@Column(name = "c_id", unique = true, nullable = false, length = 3)
	private String cId;

	@Column(name = "c_name", nullable = false, length = 10)
	private String cName;

	@Column(name = "c_type", nullable = false, length = 1)
	private String cType;

	@Column(name = "directions", length = 20)
	private String directions;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "c_id")
	private List<TbAClassItem> itemList;

	public TbAccountingClass() {

	}

	public TbAccountingClass(String cId, String cName, String cType, String directions) {
		this.cId = cId;
		this.cName = cName;
		this.cType = cType;
		this.directions = directions;
	}

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cName = cType;
	}

	public String getDirections() {
		return this.directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public List<TbAClassItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbAClassItem> itemList) {
		this.itemList = itemList;
	}

}
