package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;

import com.yesee.gov.website.exception.AccountingException;

public interface BalanceReportService {

	public String getItemBalanceList(HttpServletRequest req) throws AccountingException, Exception;

}
