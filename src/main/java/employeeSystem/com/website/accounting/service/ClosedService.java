package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;

import employeeSystem.com.website.accounting.exception.AccountingException;
import net.sf.json.JSONObject;

public interface ClosedService {

	public String getClosed(HttpServletRequest req) throws AccountingException, Exception;

	public String getClosed(String year, String createUser) throws AccountingException, Exception;

	public void updateClosed(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;
}
