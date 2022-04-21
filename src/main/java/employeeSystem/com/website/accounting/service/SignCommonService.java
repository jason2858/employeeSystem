package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employeeSystem.com.website.accounting.exception.AccountingException;
import net.sf.json.JSONObject;

public interface SignCommonService {

	public String getSignCommonList(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception;

	public String saveSignCommon(HttpServletRequest req, HttpServletResponse resp, JSONObject signCommonInfo)
			throws AccountingException, Exception;

	public String getSignCommon(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

}
