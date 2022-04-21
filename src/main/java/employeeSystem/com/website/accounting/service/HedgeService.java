package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;

import employeeSystem.com.website.accounting.exception.AccountingException;
import net.sf.json.JSONObject;

public interface HedgeService {

	public String getHedge(HttpServletRequest req) throws AccountingException, Exception;

	public String saveHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

//	public void updateHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;
}
