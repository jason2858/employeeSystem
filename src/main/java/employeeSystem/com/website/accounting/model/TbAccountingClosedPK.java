package employeeSystem.com.website.accounting.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TbAccountingClosedPK implements java.io.Serializable {

	@Column(name = "year", unique = true, nullable = false, length = 4)
	private String year;

	@Column(name = "month", unique = true, nullable = false, length = 12)
	private String month;

	public TbAccountingClosedPK() {
	}

	public TbAccountingClosedPK(String year, String month) {
		this.year = year;
		this.month = month;
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

	public String getYearAndMonth() {
		return year + String.format("%02d", Integer.parseInt(month));
	}

}
