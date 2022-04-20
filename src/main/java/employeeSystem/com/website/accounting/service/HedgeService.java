package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;

import com.yesee.gov.website.exception.AccountingException;

import net.sf.json.JSONObject;

public interface HedgeService {

	public String getHedge(HttpServletRequest req) throws AccountingException, Exception;

	public String saveHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

//	public void updateHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;
}
