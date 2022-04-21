package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.VTbReportBalance;

public interface VReportBalanceDao {

	public List<VTbReportBalance> getList(Map<String, Object> param) throws Exception;
}
