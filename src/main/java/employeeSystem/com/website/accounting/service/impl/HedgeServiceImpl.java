package com.yesee.gov.website.service.accounting.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.accounting.AClassItemDao;
import com.yesee.gov.website.dao.accounting.HedgeDao;
import com.yesee.gov.website.dao.accounting.VTbVoucherHedgeDao;
import com.yesee.gov.website.dao.accounting.VoucherHedgeDao;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.model.accounting.TbAClassItem;
import com.yesee.gov.website.model.accounting.TbHedge;
import com.yesee.gov.website.model.accounting.TbVoucherHedge;
import com.yesee.gov.website.model.accounting.VTbVoucherHedge;
import com.yesee.gov.website.service.accounting.HedgeService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("hedgeService")
public class HedgeServiceImpl implements HedgeService {

	private static final Logger logger = LogManager.getLogger(HedgeServiceImpl.class);

	@Autowired
	private VoucherHedgeDao voucherHedgeDao;

	@Autowired
	private VTbVoucherHedgeDao vTbVoucherHedgeDao;

	@Autowired
	private HedgeDao hedgeDao;

	@Autowired
	private AClassItemDao aClassItemDao;

	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");

	@Override
	public String getHedge(HttpServletRequest req) throws AccountingException, Exception {
		String hedgeNo = req.getParameter("hedge_no");
		String hedgeItem = req.getParameter("item");
		String creditDateStart = req.getParameter("credit_date_start");
		String creditDateEnd = req.getParameter("credit_date_end");
		String company = req.getParameter("company");

		return getHedge(hedgeNo, hedgeItem, creditDateStart, creditDateEnd, company);
	}

	private String getHedge(String hedgeNo, String hedgeItem, String creditDateStart, String creditDateEnd,
			String company) throws AccountingException, Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		List<VTbVoucherHedge> list = new ArrayList<VTbVoucherHedge>();

		if (!StringUtils.isEmpty(hedgeNo)) {
			param.put("entity.hedgeNo", hedgeNo);
		}

		if (!StringUtils.isEmpty(company)) {
			param.put("entity.company", company);
		}

		param.put("start", creditDateStart);
		param.put("end", creditDateEnd);

		if (!Objects.isNull(hedgeItem)) {
			String[] hedgeItemList = hedgeItem.split(",");

			// 檢查各HedgeItem c_type是否一致
			List<String> cTypeList = new ArrayList<String>();
			for (String item : hedgeItemList) {
				String cType = aClassItemDao.getAClassItemType(item);
				if (!cTypeList.isEmpty()) {
					if (!cTypeList.get(0).equals(cType)) {
						throw new AccountingException("科目類別需一致");
					}
				}
				cTypeList.add(cType);
			}

			for (String item : hedgeItemList) {
				param.put("entity.hedgeItem", item);
				List<VTbVoucherHedge> hedgeList = vTbVoucherHedgeDao.getList(param);
				for (int i = 0; i < hedgeList.size(); i++) {
					list.add(hedgeList.get(i));
				}
			}
		} else {
			throw new AccountingException("請選擇項目");
		}

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("hedgeNo", list.get(i).getHedgeNo());
			object.put("hedgeItem", list.get(i).getHedgeItem());
			object.put("amount", list.get(i).getBalance());
			object.put("directions", list.get(i).getDirections());
			object.put("creditDate", list.get(i).getCreditDate());
			result.add(object);
		}
		return result.toString();
	}

	@Override
	public String saveHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
		JSONArray data = (JSONArray) body.get("data");
		JSONObject footer = (JSONObject) body.get("footer");
		String footerHedgeItem = (String) footer.get("hedge_item");
		String footerAmount = (String) footer.get("amount");
		String footerDirections = (String) footer.get("directions");
		String createUser = req.getSession().getAttribute("Account").toString();

		if (!Objects.isNull(footerDirections)) {
			footerDirections = "";
		}

		return saveHedge(data, footerHedgeItem, footerAmount, footerDirections, createUser);
	}

	private String saveHedge(JSONArray data, String footerHedgeItem, String footerAmount, String footerDirections,
			String createUser) throws AccountingException, Exception {
		// footer tb_hedge
		String hedgeNo = createHVoucherNo();
		Integer balance = Integer.parseInt(footerAmount);
		Timestamp createDate = new Timestamp(System.currentTimeMillis());

		TbHedge tbHedge = new TbHedge(hedgeNo, balance, createUser, createDate);
		hedgeDao.save(tbHedge);

		// footer tb_voucher_hedge
		Integer id = null;
		TbHedge hedge = new TbHedge();
		hedge.setHedgeNo(hedgeNo);
		String hVoucherNo = hedgeNo;
		TbAClassItem hedgeItem = new TbAClassItem();
		hedgeItem.setiId(footerHedgeItem);
		Integer amount = balance;
		String directions = footerDirections;
		String creditDate = sdFormat.format(currentTimestamp);
		String expStatus = "U";
		Timestamp expDate = null;
		String modiUser = null;
		Timestamp modiDate = null;

		TbVoucherHedge voucherHedge = new TbVoucherHedge(id, hedge, hVoucherNo, hedgeItem, amount, directions,
				creditDate, expStatus, expDate, createUser, createDate, modiUser, modiDate);
		voucherHedgeDao.save(voucherHedge);

		// data內各hedge_no
		for (int i = 0; i < data.size(); i++) {
			JSONObject object = (JSONObject) data.get(i);

			id = null;
			hedgeNo = (String) object.get("hedge_no");
			hedge = new TbHedge();
			hedge.setHedgeNo(hedgeNo);
			hedgeItem = new TbAClassItem();
			hedgeItem.setiId((String) object.get("hedge_item"));
			amount = Integer.parseInt((String) object.get("amount"));
			directions = (String) object.get("directions");

			voucherHedge = new TbVoucherHedge(id, hedge, hVoucherNo, hedgeItem, amount, directions, creditDate,
					expStatus, expDate, createUser, createDate, modiUser, modiDate);
			voucherHedgeDao.save(voucherHedge);

			// 更新tb_hedge.balance的值為tb_hedge.balance - amount的數值
			TbHedge updateHedge = hedgeDao.findById(hedgeNo);
			updateHedge.setBalance(updateHedge.getBalance() - amount);
			hedgeDao.update(updateHedge);
		}
		return hVoucherNo;
	}

	private String createHVoucherNo() throws Exception {

		String date = sdFormat.format(currentTimestamp);
		String hVoucherNo = null;

		if (voucherHedgeDao.findToday() == null || voucherHedgeDao.findToday().isEmpty()) {
			hVoucherNo = "H" + date + "001";

		} else {
			String oldNo = voucherHedgeDao.findToday().get(0).gethVoucherNo();
			int num = Integer.parseInt(oldNo.substring(9, oldNo.length())) + 1;
			String numString = String.format("%03d", num);
			hVoucherNo = "H" + date + numString;
		}

		return hVoucherNo;
	}

//	@Override
//	public void updateHedge(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
//		JSONArray hedgeList = (JSONArray) body.get("hedge_list");
//		updateHedge(hedgeList);
//	}
//
//	public void updateHedge(JSONArray hedgeList) throws AccountingException, Exception {
//
//		for (int i = 0; i < hedgeList.size(); i++) {
//			Map<String, Object> param = new HashMap<String, Object>();
//			String hedgeNo = (String) hedgeList.get(i);
//
//			param.put("entity.hedgeNo.hedgeNo", hedgeNo);
//			
//		}
//	}

}
