package employeeSystem.com.website.system.pojo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ScheduledLeaveVo {

	private String employees;
	private String year;
	private List<Map<String, String>> map;

	public String getEmployees() {
		return employees;
	}

	public void setEmployees(String employees) {
		this.employees = employees;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public List<Map<String, String>> getMap() {
		return map;
	}

	public void setMap(List<Map<String, String>> map) {
		this.map = map;
	}

}
