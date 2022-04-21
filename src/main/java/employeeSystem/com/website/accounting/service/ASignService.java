package employeeSystem.com.website.accounting.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import employeeSystem.com.website.accounting.exception.AccountingException;
import net.sf.json.JSONObject;

public interface ASignService {

	public String getSignList(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

	public void saveVoucherSign(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

	public String updateVoucherSign(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

	public String getSignRoleList() throws AccountingException, Exception;
}
