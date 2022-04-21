package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.VTbReportHedge;

public interface VReportHedgeDao {

	public List<VTbReportHedge> getList(Map<String, Object> param) throws Exception;

	public Map<String, Integer> getBalance(String start, String end, String iId);

}
