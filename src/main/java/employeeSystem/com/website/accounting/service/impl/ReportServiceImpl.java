package employeeSystem.com.website.accounting.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import employeeSystem.com.website.accounting.dao.VReportBalanceDao;
import employeeSystem.com.website.accounting.exception.AccountingException;
import employeeSystem.com.website.accounting.model.VTbReportBalance;
import employeeSystem.com.website.accounting.service.ReportService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

	@Autowired
	VReportBalanceDao vReportBalanceDao;

	@Override
	public String getReceivableList(HttpServletRequest req) throws AccountingException, Exception {
		String compamy = req.getParameter("company");
		String item = req.getParameter("item");
		String creditDateStart = req.getParameter("credit_date_start");
		String creditDateEnd = req.getParameter("credit_date_end");

		return getReceivableList(compamy, item, creditDateStart, creditDateEnd);
	}

	private String getReceivableList(String company, String item, String creditDateStart, String creditDateEnd)
			throws Exception {

		// check
		if (StringUtils.isEmpty(company) || StringUtils.isEmpty(item) || StringUtils.isEmpty(creditDateStart)
				|| StringUtils.isEmpty(creditDateEnd)) {
			throw new AccountingException("欄位不能為空值");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.company", company);
		param.put("entity.item", item);
		param.put("entity.creditDate", creditDateStart.replace("-", "") + "&&&" + creditDateEnd.replace("-", ""));

		List<VTbReportBalance> list = new ArrayList<VTbReportBalance>();
		list = vReportBalanceDao.getList(param);

		if (CollectionUtils.isEmpty(list)) {
			throw new AccountingException("查無資料");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("predictDate", list.get(i).getPredictDate());
			object.put("customer", list.get(i).getCustomer());
			object.put("cusTaxId", list.get(i).getCusTaxId());
			object.put("hedgeNo", list.get(i).getHedgeNo());
			object.put("voucherNo", list.get(i).getVoucherNo());
			object.put("amount", list.get(i).getBalance());
			object.put("directions", list.get(i).getDirections());
			result.add(object);
		}
		return result.toString();
	}

	@Override
	public String getBalanceList(HttpServletRequest req) throws AccountingException, Exception {
		String compamy = req.getParameter("company");
		String item = req.getParameter("item");
		String creditDateStart = req.getParameter("credit_date_start");
		String creditDateEnd = req.getParameter("credit_date_end");
		String predictDateEnd = req.getParameter("predict_date_end");

		return getBalanceList(compamy, item, creditDateStart, creditDateEnd, predictDateEnd);
	}

	private String getBalanceList(String company, String item, String creditDateStart, String creditDateEnd,
			String predictDateEnd) throws Exception {
		// check
		if (StringUtils.isEmpty(company) || StringUtils.isEmpty(item) || StringUtils.isEmpty(creditDateStart)
				|| StringUtils.isEmpty(creditDateEnd) || StringUtils.isEmpty(predictDateEnd)) {
			throw new AccountingException("欄位不能為空值");
		}

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.company", company);
		param.put("entity.item", item);
		param.put("entity.creditDate", creditDateStart.replace("-", "") + "&&&" + creditDateEnd.replace("-", ""));
		param.put("entity.predictDate", "19110101" + "&&&" + predictDateEnd.replace("-", ""));

		List<VTbReportBalance> list = new ArrayList<VTbReportBalance>();
		list = vReportBalanceDao.getList(param);

		if (list == null || list.size() == 0) {
			throw new AccountingException("查無資料");
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("predictDate", list.get(i).getPredictDate());
			object.put("customer", list.get(i).getCustomer());
			object.put("cusTaxId", list.get(i).getCusTaxId());
			object.put("creditDate", list.get(i).getCreditDate());
			object.put("item", list.get(i).getItem());
			object.put("voucherNo", list.get(i).getVoucherNo());
			object.put("amount", list.get(i).getBalance());
			object.put("directions", list.get(i).getDirections());
			result.add(object);
		}
		return result.toString();
	}

}
