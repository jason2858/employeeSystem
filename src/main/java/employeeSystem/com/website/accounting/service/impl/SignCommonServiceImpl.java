package com.yesee.gov.website.service.accounting.impl;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.accounting.SignCommonDao;
import com.yesee.gov.website.dao.accounting.SignCommonSetDao;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.accounting.TbSignCommon;
import com.yesee.gov.website.model.accounting.TbSignCommonSet;
import com.yesee.gov.website.model.accounting.TbSignRole;
import com.yesee.gov.website.service.accounting.SignCommonService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("SignCommonService")
public class SignCommonServiceImpl implements SignCommonService {

	private static final Logger logger = LogManager.getLogger(SignCommonServiceImpl.class);

	@Autowired
	public SignCommonDao signCommonDao;

	@Autowired
	public SignCommonSetDao signCommonSetDao;

	@Override
	public String getSignCommonList(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		return getSignCommonList();
	}

	private String getSignCommonList() throws AccountingException, Exception {

		List<TbSignCommon> list = signCommonDao.getSignCommonList();
		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}
		JSONObject commonList = new JSONObject();
		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("signNo", list.get(i).getSignNo());
			object.put("signName", list.get(i).getSignName());
			result.add(object);
		}
		if (list.isEmpty()) {
			commonList.put("common", null);
		} else {
			commonList.put("common", result);
		}

		return commonList.toString();
	}

	@Override
	public String saveSignCommon(HttpServletRequest req, HttpServletResponse resp, JSONObject signCommonInfo)
			throws AccountingException, Exception {
		String signName = (String) signCommonInfo.get("sign_name");
		List<Map<String, String>> set = (List<Map<String, String>>) signCommonInfo.get("set");
		String account = req.getSession().getAttribute("Account").toString();
		return saveSignCommon(account, signName, set);
	}

	private String saveSignCommon(String account, String signName, List<Map<String, String>> set)
			throws AccountingException, Exception {

		List<TbSignCommon> check = signCommonDao.getSignCommon(signName);

		if (CollectionUtils.isEmpty(check)==false) {
			throw new AccountingException("該簽程名稱已存在");
		}

		TbSignCommon tbSignCommon = new TbSignCommon(signName, account, Calendar.getInstance().getTime());

		signCommonDao.save(tbSignCommon);

		for (int i = 0; i < set.size(); i++) {
			TbSignCommon newSignName = new TbSignCommon();
			newSignName.setSignNo("1");
			newSignName.setSignName(signName);

			TbEmployees newSignUser = new TbEmployees();
			newSignUser.setUsername(set.get(i).get("sign_user"));

			TbSignRole newRoleId = new TbSignRole();
			newRoleId.setRoleId(set.get(i).get("role_id"));

			TbSignCommonSet tbSignCommonSet = new TbSignCommonSet(newSignName, newRoleId, newSignUser, account,
					Calendar.getInstance().getTime());

			signCommonSetDao.save(tbSignCommonSet);
		}

		return "success";
	}

	@Override
	public String getSignCommon(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception {
		String signName = req.getParameter("sign_name");
		return getSignCommon(signName);
	}

	private String getSignCommon(String signName) throws AccountingException, Exception {

		if (signName == null || signName == "") {
			throw new AccountingException("請輸入簽呈名稱");
		}

		List<TbSignCommonSet> list = signCommonSetDao.getSignCommonSet(signName);

		if (list == null || list.size() == 0 || list.isEmpty()) {
			throw new AccountingException("查無資料");
		}

		JSONObject commonList = new JSONObject();
		JSONArray result = new JSONArray();

		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("roleId", list.get(i).getRoleId().getRoleId());
			object.put("role", list.get(i).getRoleId().getRole().getName());
			object.put("signUser", list.get(i).getSignUser().getUsername());
			result.add(object);
		}
		commonList.put("common", result);

		return commonList.toString();
	}
}
