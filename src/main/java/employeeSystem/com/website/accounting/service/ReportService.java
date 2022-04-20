package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;

import com.yesee.gov.website.exception.AccountingException;

public interface ReportService {

	public String getReceivableList(HttpServletRequest req) throws AccountingException, Exception;

	public String getBalanceList(HttpServletRequest req) throws AccountingException, Exception;

//    public void updateHedgeNo(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

}
