package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;

import employeeSystem.com.website.accounting.exception.AccountingException;

public interface ReportService {

	public String getReceivableList(HttpServletRequest req) throws AccountingException, Exception;

	public String getBalanceList(HttpServletRequest req) throws AccountingException, Exception;

//    public void updateHedgeNo(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

}
