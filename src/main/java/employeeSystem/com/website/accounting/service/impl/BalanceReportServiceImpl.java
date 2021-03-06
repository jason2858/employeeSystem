package employeeSystem.com.website.accounting.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import employeeSystem.com.website.accounting.dao.AClassItemDao;
import employeeSystem.com.website.accounting.dao.AccountingBalanceDao;
import employeeSystem.com.website.accounting.dao.AccountingClosedDao;
import employeeSystem.com.website.accounting.dao.VReportHedgeDao;
import employeeSystem.com.website.accounting.dao.VTbVoucherDetailDao;
import employeeSystem.com.website.accounting.dao.VoucherHeadDao;
import employeeSystem.com.website.accounting.exception.AccountingException;
import employeeSystem.com.website.accounting.model.TbAccountingBalance;
import employeeSystem.com.website.accounting.model.TbAccountingClosed;
import employeeSystem.com.website.accounting.model.VTbReportHedge;
import employeeSystem.com.website.accounting.service.BalanceReportService;
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
			throw new AccountingException("?????????????????????");
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

		// ?????????=1 ?????????=2
		if (fun.equals("1")) {
			// status??????
			List<String> status = new ArrayList<String>();
			status.add("0");
			status.add("1");
			status.add("2");
			param.put("entity.status", status);
			// ????????????????????????
			List<TbAccountingClosed> closedList = new ArrayList<TbAccountingClosed>();

			closedList = accountingClosedDao.getLastClosedMonth();
			String lastClosedMonth = closedList.get(0).getTbAccountingClosedPK().getYearAndMonth();

			// ??????????????????
			for (int i = 0; i < item.size(); i++) {
				// ?????????item???????????????
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
			// ???????????????????????????
			List<TbAccountingClosed> closedList = new ArrayList<TbAccountingClosed>();

			closedList = accountingClosedDao.getLastClosedMonth();

			if (CollectionUtils.isEmpty(closedList)) {

				throw new AccountingException("??????????????????");
			}

			String lastClosedMonth = closedList.get(0).getTbAccountingClosedPK().getYearAndMonth();

			Integer last = Integer.parseInt(lastClosedMonth + 31);
			Integer start = Integer.parseInt(creditDateStart);
			Integer end = Integer.parseInt(creditDateEnd);
			if (start > last || end > last) {

				throw new AccountingException("??????????????????");
			}
			// status??????
			param.put("entity.status", "2");
			Integer year = Integer.parseInt(lastClosedMonth.substring(0, 4));
			Integer month = Integer.parseInt(lastClosedMonth.substring(4, 6));
			// ?????????item???????????????
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
