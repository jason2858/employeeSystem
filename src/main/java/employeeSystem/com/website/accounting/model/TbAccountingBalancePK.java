package com.yesee.gov.website.model.accounting;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class TbAccountingBalancePK implements java.io.Serializable {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "i_id", nullable = false, insertable = true, updatable = true)
	private TbAClassItem iId;

	@Column(name = "year", unique = true, nullable = false, length = 4)
	private String year;

	@Column(name = "month", unique = true, nullable = false, length = 12)
	private String month;

	public TbAccountingBalancePK() {
	}

	public TbAccountingBalancePK(TbAClassItem iId, String year, String month) {
		this.iId = iId;
		this.year = year;
		this.month = month;
	}

	public TbAClassItem getiId() {
		return iId;
	}

	public void setiId(TbAClassItem iId) {
		this.iId = iId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
}
