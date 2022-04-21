package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;

import employeeSystem.com.website.accounting.exception.AccountingException;

public interface BalanceReportService {

	public String getItemBalanceList(HttpServletRequest req) throws AccountingException, Exception;

}
