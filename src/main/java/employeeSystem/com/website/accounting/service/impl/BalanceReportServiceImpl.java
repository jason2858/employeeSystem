package employeeSystem.com.website.accounting.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.yesee.gov.website.dao.accounting.AClassItemDao;
import com.yesee.gov.website.dao.accounting.AccountingBalanceDao;
import com.yesee.gov.website.dao.accounting.AccountingClosedDao;
import com.yesee.gov.website.dao.accounting.VReportHedgeDao;
import com.yesee.gov.website.dao.accounting.VTbVoucherDetailDao;
import com.yesee.gov.website.dao.accounting.VoucherDetailDao;
import com.yesee.gov.website.dao.accounting.VoucherHeadDao;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.model.accounting.TbAccountingBalance;
import com.yesee.gov.website.model.accounting.TbAccountingClosed;
import com.yesee.gov.website.model.accounting.VTbReportHedge;
import com.yesee.gov.website.service.accounting.BalanceReportService;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("balanceReportService")
public class BalanceReportServiceImpl implements BalanceReportService {

	@Autowired
	VReportHedgeDao vReportHedgeDao;

	@Autowired
	AccountingClosedDao accountingClosedDao;

	@Autowired
	AccountingBalanceDao accountingBalanceDao;

	@Autowired
	VoucherHeadDao voucherHeadDao;

	@Autowired
	VTbVoucherDetailDao vTbvoucherDetailDao;

	@Autowired
	AClassItemDao aClassItemDao;

	@Override
	public String getItemBalanceList(HttpServletRequest req) throws AccountingException, Exception {
		String company = req.getParameter("company");
		String item = req.getParameter("item");
		String creditDateStart = req.getParameter("credit_date_start");
		String creditDateEnd = req.getParameter("credit_date_end");
		String fun = req.getParameter("fun");
		return getItemBalanceList(company, item, creditDateStart, creditDateEnd, fun);
	}

	private String getItemBalanceList(String company, String itemInput, String creditDateStart, String creditDateEnd,
			String fun) throws AccountingException, Exception {

		if (StringUtils.isEmpty(company) || StringUtils.isEmpty(itemInput) || StringUtils.isEmpty(creditDateStart)
				|| StringUtils.isEmpty(creditDateEnd)) {
			throw new AccountingException("欄位不能為空值");
		}

		creditDateStart = creditDateStart.replace("-", "");
		creditDateEnd = creditDateEnd.replace("-", "");

		String[] itemString = itemInput.split(",");
		List<String> item = new ArrayList<String>(Arrays.asList(itemString));

		Map<String, Object> param = new HashMap<String, Object>();

		Map<String, Object> balance = new HashMap<String, Object>();

		param.put("entity.company", company);
		param.put("entity.item", item);
		param.put("entity.creditDate", creditDateStart + "&&&" + creditDateEnd);

		// 分類帳=1 餘額表=2
		if (fun.equals("1")) {
			// status條件
			List<String> status = new ArrayList<String>();
			status.add("0");
			status.add("1");
			status.add("2");
			param.put("entity.status", status);
			// 查詢最後關帳月份
			List<TbAccountingClosed> closedList = new ArrayList<TbAccountingClosed>();

			closedList = accountingClosedDao.getLastClosedMonth();
			String lastClosedMonth = closedList.get(0).getTbAccountingClosedPK().getYearAndMonth();

			// 前期餘額計算
			for (int i = 0; i < item.size(); i++) {
				// 取得各item前期餘額值
				Integer year = Integer.parseInt(lastClosedMonth.substring(0, 4));
				Integer month = Integer.parseInt(lastClosedMonth.substring(4, 6));
				Integer lastBalance = 0;
				Integer sum = 0;
				List<TbAccountingBalance> abList = accountingBalanceDao.getBOne(month.toString(), year.toString(),
						item.get(i));

				if (!CollectionUtils.isEmpty(abList) || !StringUtils.isEmpty(abList.get(0).getBalance())) {
					lastBalance = abList.get(0).getBalance();
				}

				if (month == 12) {
					year = year + 1;
					month = 1;
				} else {
					month = month + 1;
				}

				Integer end = Integer.parseInt(creditDateStart) - 1;
				Integer start = Integer.parseInt(year.toString() + String.format("%02d", month) + "01");

				if (end > start) {
					sum = vReportHedgeDao.getBalance(start.toString(), end.toString(), item.get(i)).get("total");
				}

				balance.put(item.get(i), lastBalance + sum);
			}

		} else if (fun.equals("2")) {
			// 查詢是否有關帳資料
			List<TbAccountingClosed> closedList = new ArrayList<TbAccountingClosed>();

			closedList = accountingClosedDao.getLastClosedMonth();

			if (CollectionUtils.isEmpty(closedList)) {

				throw new AccountingException("無已關帳資料");
			}

			String lastClosedMonth = closedList.get(0).getTbAccountingClosedPK().getYearAndMonth();

			Integer last = Integer.parseInt(lastClosedMonth + 31);
			Integer start = Integer.parseInt(creditDateStart);
			Integer end = Integer.parseInt(creditDateEnd);
			if (start > last || end > last) {

				throw new AccountingException("無已關帳資料");
			}
			// status條件
			param.put("entity.status", "2");
			Integer year = Integer.parseInt(lastClosedMonth.substring(0, 4));
			Integer month = Integer.parseInt(lastClosedMonth.substring(4, 6));
			// 取得各item前期餘額值
			List<TbAccountingBalance> abList = accountingBalanceDao.getBList(month.toString(), year.toString());
			for (int i = 0; i < abList.size(); i++) {
				balance.put(abList.get(i).getTbAccountingBalancePK().getiId().getiId(), abList.get(i).getBalance());
			}
		}

		List<VTbReportHedge> list = new ArrayList<VTbReportHedge>();
		list = vReportHedgeDao.getList(param);

		if (CollectionUtils.isEmpty(list)) {
			JSONArray result = new JSONArray();
			for (int i = 0; i < item.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("itemNo", item.get(i));
				String name = null;
				if (aClassItemDao.findById(item.get(i)) != null) {
					name = aClassItemDao.findById(item.get(i)).getiName();
				}
				object.put("itemName", name);
				object.put("item", new ArrayList<>());
				object.put("balance", balance.get(item.get(i)));
				result.add(object);
			}

			return result.toString();
		}

		Map<Object, List<VTbReportHedge>> reports = list.stream()
				.collect(Collectors.groupingBy(report -> report.getItem() + "," + report.getItemName()));

		List<Object> a = reports.entrySet().stream().distinct().map(e -> {
			String[] key = e.getKey().toString().split(",");
			String itemNo = key[0];
			String itemName = key[1];

			List<VTbReportHedge> value = e.getValue();
			JSONArray objectList = new JSONArray();
			for (int i = 0; i < value.size(); i++) {
				JSONObject object = new JSONObject();

				object.put("voucherNo", value.get(i).getVoucherNo());
				object.put("cusTaxId", value.get(i).getCusTaxId());
				object.put("customer", value.get(i).getCustomer());
				object.put("hedgeNo", value.get(i).getHedgeNo());
				object.put("project", value.get(i).getProject());
				object.put("directions", value.get(i).getDirections());
				object.put("amount", value.get(i).getAmount());
				object.put("company", value.get(i).getCompany());
				object.put("signStatus", value.get(i).getStatus());
				if (fun.equals("1")) {
					object.put("type", value.get(i).getType());
				}

				objectList.add(object);
			}
			JSONObject itemList = new JSONObject();
			itemList.put("itemNo", itemNo);
			itemList.put("itemName", itemName);
			itemList.put("item", objectList);
			itemList.put("balance", balance.get(itemNo));
			return itemList;
		}).collect(Collectors.toList());

		JSONArray result = new JSONArray();
		for (int i = 0; i < a.size(); i++) {
			result.add(a.get(i));
		}

		return result.toString();
	}

}
