package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yesee.gov.website.exception.AccountingException;

import net.sf.json.JSONObject;

public interface SignCommonService {

	
	public String getSignCommonList(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

	public String saveSignCommon(HttpServletRequest req, HttpServletResponse resp, JSONObject signCommonInfo)
			throws AccountingException, Exception;

	public String getSignCommon(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

}
