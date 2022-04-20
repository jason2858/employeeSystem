package com.yesee.gov.website.service.accounting.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.yesee.gov.website.dao.accounting.AClassItemDao;
import com.yesee.gov.website.dao.accounting.AccountingBalanceDao;
import com.yesee.gov.website.dao.accounting.AccountingClassDao;
import com.yesee.gov.website.dao.accounting.AccountingClosedDao;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.model.accounting.TbAClassItem;
import com.yesee.gov.website.model.accounting.TbAccountingBalance;
import com.yesee.gov.website.model.accounting.TbAccountingBalancePK;
import com.yesee.gov.website.model.accounting.TbAccountingClass;
import com.yesee.gov.website.model.accounting.TbAccountingClosed;
import com.yesee.gov.website.service.accounting.AccountingService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("accountingService")
public class AccountingServiceImpl implements AccountingService {
	private static final Logger logger = LogManager.getLogger(AccountingServiceImpl.class);

	@Autowired
	private AccountingClassDao accountingClassDao;

	@Autowired
	private AClassItemDao aClassItemDao;

	@Autowired
	private AccountingBalanceDao accountingBalanceDao;

	@Autowired
	private AccountingClosedDao accountingClosedDao;

	@Override
	public String getManager(HttpServletRequest req) throws AccountingException, Exception {
		String cId = req.getParameter("c_id");
		String iId = req.getParameter("i_id");
		String iName = req.getParameter("i_name");
		return this.getManager(cId, iId, iName);
	}

	// 待修改
	private String getManager(String cId, String iId, String iName) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		if (!Objects.isNull(cId)) {
			param.put("entity.cId.cId", cId);
		}
		if (!Objects.isNull(iId)) {
			param.put("entity.iId", "%" + iId + "%");
		}
		if (!Objects.isNull(iName)) {
			param.put("entity.iName", "%" + iName + "%");
		}
		List<TbAClassItem> list = new ArrayList<TbAClassItem>();
		list = aClassItemDao.getList(param);
		System.out.println("stringlike= :" + list.size());

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		List<TbAccountingClass> classList = accountingClassDao.getList(new HashMap<String, Object>());

		List<String> existClass = new ArrayList<String>();

		for (int i = 0; i < list.size(); i++) {
			if (!existClass.contains(list.get(i).getcId().getcName())) {
				existClass.add(list.get(i).getcId().getcName());
			}
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < classList.size(); i++) {
			if (existClass.contains(classList.get(i).getcName())) {
				JSONObject classListUnit = new JSONObject();
				classListUnit.put("cName", classList.get(i).getcName());
				JSONArray array = new JSONArray();
				for (int j = 0; j < list.size(); j++) {
					if (classList.get(i).getcId().equals(list.get(j).getcId().getcId())) {
						JSONObject object = new JSONObject();
						object.put("iId", list.get(j).getiId());
						object.put("iName", list.get(j).getiName());
						object.put("directions", list.get(j).getDirections());
						object.put("enable", list.get(j).getEnable());
						array.add(object);
						list.remove(j);
						j--;
					}
				}
				classListUnit.put("itemList", array);
				result.add(classListUnit);
			}
		}

		return result.toString();
	}

	@Override
	public String getItemList(HttpServletRequest req) throws AccountingException, Exception {
		String cId = req.getParameter("c_id");
		String cType = req.getParameter("c_type");

		return this.getItemList(cId, cType);
	}

	private String getItemList(String cId, String cType) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		if (!StringUtils.isEmpty(cId)) {
			param.put("entity.cId", cId);
		}

		if (!StringUtils.isEmpty(cType)) {
			param.put("entity.cType", cType);
		}

		List<TbAccountingClass> list = new ArrayList<TbAccountingClass>();
		list = accountingClassDao.getList(param);
		System.out.println("string = :" + list.size());

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			List<TbAClassItem> itemList = list.get(i).getItemList();
			for (TbAClassItem item : itemList) {
				JSONObject object = new JSONObject();
				object.put("iId", item.getiId());
				object.put("iName", item.getiName());
				object.put("enable", item.getEnable());
				result.add(object);
			}
		}

		return result.toString();
	}

	@Override
	public String getClassList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		List<TbAccountingClass> list = new ArrayList<TbAccountingClass>();
		list = accountingClassDao.getList(param);
		System.out.println("string = :" + list.size());

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("cId", list.get(i).getcId());
			object.put("cName", list.get(i).getcName());
			result.add(object);
		}

		return result.toString();
	}

	@Override
	public void itemSave(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
		String iId = (String) body.get("i_id");
		String iName = (String) body.get("i_name");
		String cIdString = (String) body.get("c_id");
		String amount = (String) body.get("amount");
		String directions = (String) body.get("directions");
		String createUser = req.getSession().getAttribute("Account").toString();

		if (!StringUtils.isNumeric(iId) | iId.length() != 4) {
			throw new AccountingException("i_id需為四碼數字");
		}

		if (!StringUtils.isNumeric(cIdString) | cIdString.length() != 2) {
			throw new AccountingException("c_id需為兩碼數字");
		}

		if (!iId.substring(0, 2).equals(cIdString)) {
			throw new AccountingException("i_id前兩碼數字需等同c_id");
		}

		if (StringUtils.isEmpty(amount)) {
			throw new AccountingException("需輸入金額");
		}

		if (Objects.isNull(directions)) {
			directions = "";
		}

		this.itemSave(iId, iName, cIdString, amount, directions, createUser);
		return;
	}

	private void itemSave(String iId, String iName, String cIdString, String amount, String directions,
			String createUser) throws AccountingException, Exception {

		if (aClassItemDao.findById(iId) != null) {
			throw new AccountingException("請檢查項目編號是否已存在");
		}

		TbAccountingClass cId = new TbAccountingClass();
		cId.setcId(cIdString);
		String enable = "Y";
		Timestamp createDate = new Timestamp(System.currentTimeMillis());

		TbAClassItem aClassItem = new TbAClassItem(iId, iName, cId, directions, enable, createUser, createDate);
		aClassItemDao.save(aClassItem);

		List<TbAccountingClosed> closeList = accountingClosedDao.getLastClosedMonth();

		if (closeList == null || closeList.size() == 0) {
			throw new AccountingException("無已完全關帳月份");
		}

		TbAccountingClosed lastClosed = closeList.get(0);
		String lastYear = lastClosed.getTbAccountingClosedPK().getYear();
		String lastMonth = lastClosed.getTbAccountingClosedPK().getMonth();

		Integer balance = Integer.parseInt(amount);

		TbAccountingBalancePK pk = new TbAccountingBalancePK(aClassItem, lastYear, lastMonth);
		TbAccountingBalance accountingBalance = new TbAccountingBalance(pk, balance, createUser, createDate);
		accountingBalanceDao.save(accountingBalance);
	}

	@Override
	public void itemUpdate(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception {
		String iId = (String) body.get("i_id");
		String iName = (String) body.get("i_name");
		String directions = (String) body.get("directions");
		if (Objects.isNull(directions)) {
			directions = "";
		}

		this.itemUpdate(iId, iName, directions);
		return;
	}

	public void itemUpdate(String iId, String iName, String directions) throws AccountingException, Exception {
		try {
			TbAClassItem updateAClassItem = aClassItemDao.findById(iId);
			updateAClassItem.setiName(iName);
			updateAClassItem.setDirections(directions);

			aClassItemDao.update(updateAClassItem);
		} catch (Exception e) {
			logger.error("error : ", e);
			throw new AccountingException("更新失敗");
		}
		return;
	}

	@Override
	public void itemDelete(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception {
		String iId = (String) body.get("i_id");
		String enable = (String) body.get("enable");

		this.itemDelete(iId, enable);
		return;
	}

	private void itemDelete(String iId, String enable) throws AccountingException, Exception {
		try {
			TbAClassItem deleteeAClassItem = aClassItemDao.findById(iId);
			deleteeAClassItem.setEnable(enable);

			aClassItemDao.update(deleteeAClassItem);
		} catch (Exception e) {
			logger.error("error : ", e);
			throw new AccountingException("操作失敗");
		}
	}

	@Override
	public void classSave(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception {
		String cId = (String) body.get("c_id");
		String cName = (String) body.get("c_name");
		String cType = (String) body.get("c_type");
		String directions = (String) body.get("directions");

		if (!StringUtils.isNumeric(cId) | cId.length() != 2) {
			throw new AccountingException("c_id需為兩碼數字");
		}

		if (Objects.isNull(directions)) {
			directions = "";
		}

		this.classSave(cId, cName, cType, directions);
		return;
	}

	private void classSave(String cId, String cName, String cType, String directions)
			throws AccountingException, Exception {

		if (accountingClassDao.findById(cId) != null) {
			throw new AccountingException("請檢查類別編號是否已存在");
		}

		TbAccountingClass accountingClass = new TbAccountingClass(cId, cName, cType, directions);
		accountingClassDao.save(accountingClass);

		return;
	}

	@Override
	public void classUpdate(HttpServletRequest req, @RequestBody JSONObject body)
			throws AccountingException, Exception {
		String cId = (String) body.get("c_id");
		String cName = (String) body.get("c_name");
		String directions = (String) body.get("directions");

		if (Objects.isNull(directions)) {
			directions = "";
		}

		this.classUpdate(cId, cName, directions);
		return;
	}

	public void classUpdate(String cId, String cName, String directions) throws AccountingException, Exception {
		try {
			TbAccountingClass updateAccountingClass = accountingClassDao.findById(cId);
			updateAccountingClass.setcName(cName);
			updateAccountingClass.setDirections(directions);

			accountingClassDao.update(updateAccountingClass);
		} catch (Exception e) {
			logger.error("error : ", e);
			throw new AccountingException("更新失敗");
		}
		return;
	}

	@Override
	public String example(HttpServletRequest req) throws AccountingException, Exception {
		String cId = req.getParameter("c_id");
		return this.example(cId);
	}

	private String example(String cId) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		// 一般查詢
		param.put("entity.cId", cId);
		List<TbAccountingClass> list = new ArrayList<TbAccountingClass>();
		list = accountingClassDao.getList(param);
		System.out.println("string = :" + list.size());
		// 模糊查詢
		param.put("entity.cId", "%" + cId + "%");
		list = new ArrayList<TbAccountingClass>();
		list = accountingClassDao.getList(param);
		System.out.println("stringlike= :" + list.size());
		// sql in
		List<String> dList = new ArrayList<String>();
		dList.add(cId);
		param.put("entity.cId", dList);
		list = new ArrayList<TbAccountingClass>();
		list = accountingClassDao.getList(param);
		System.out.println("list = :" + list.size());

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		JSONObject result = new JSONObject();
		for (int i = 0; i < list.size(); i++) {
			result.put("cName", list.get(i).getcName());
			List<TbAClassItem> itemList = list.get(i).getItemList();
			JSONArray array = new JSONArray();
			for (TbAClassItem item : itemList) {
				JSONObject object = new JSONObject();
				object.put("iId", item.getiId());
				object.put("iName", item.getiName());
				object.put("directions", item.getDirections());
				array.add(object);
			}
			result.put("itemList", array);
		}

		return result.toString();
	}

}
