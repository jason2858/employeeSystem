package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.exception.SystemOutException;


import net.sf.json.JSONObject;

public interface PunchNoticeService {

	public String getPunchList(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

	public String sendNoticeMail(HttpServletRequest req, HttpServletResponse resp, JSONObject accountInfo)
			throws SystemOutException, Exception;

}
