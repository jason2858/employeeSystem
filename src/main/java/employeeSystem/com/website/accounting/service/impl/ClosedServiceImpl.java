package employeeSystem.com.website.accounting.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.accounting.dao.AccountingClosedDao;
import employeeSystem.com.website.accounting.exception.AccountingException;
import employeeSystem.com.website.accounting.model.TbAccountingClosed;
import employeeSystem.com.website.accounting.model.TbAccountingClosedPK;
import employeeSystem.com.website.accounting.service.ClosedService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("closedService")
public class ClosedServiceImpl implements ClosedService {

	private static final Logger logger = LogManager.getLogger(HedgeServiceImpl.class);

	@Autowired
	AccountingClosedDao accountingClosedDao;

	@Override
	public String getClosed(HttpServletRequest req) throws AccountingException, Exception {
		String year = req.getParameter("year");
		String createUser = req.getSession().getAttribute("Account").toString();

		if (Integer.parseInt(year) > Calendar.getInstance().get(Calendar.YEAR)) {
			throw new AccountingException("只可查詢小於(含)今年的關帳狀態");
		}

		return getClosed(year, createUser);
	}

	@Override
	public String getClosed(String year, String createUser) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.tbAccountingClosedPK.year", year);

		List<TbAccountingClosed> list = new ArrayList<TbAccountingClosed>();
		list = accountingClosedDao.getList(param);

		if (list == null || list.size() == 0) {

			if (Integer.parseInt(year) != Calendar.getInstance().get(Calendar.YEAR)) {
				throw new AccountingException("該年份無紀錄");
			}

			for (int i = 1; i < 13; i++) {
				Integer monthInteger = i;
				String month = monthInteger.toString();
				TbAccountingClosedPK pk = new TbAccountingClosedPK(year, month);
				String Status = "O";
				Timestamp createDate = new Timestamp(System.currentTimeMillis());

				TbAccountingClosed tbAccountingClosed = new TbAccountingClosed(pk, Status, createUser, createDate);
				accountingClosedDao.save(tbAccountingClosed);
			}
			list = accountingClosedDao.getList(param);
		}

		JSONArray result = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("month", list.get(i).getTbAccountingClosedPK().getMonth());
			object.put("status", list.get(i).getStatus());
			result.add(object);

		}

		return result.toString();
	}

	@Override
	public void updateClosed(HttpServletRequest req, JSONObject body) throws AccountingException, Exception {
		String year = (String) body.get("year");
		String month = (String) body.get("month");
		String status = (String) body.get("status");

		if (Integer.parseInt(year) > Calendar.getInstance().get(Calendar.YEAR)
				|| (Integer.parseInt(year) == Calendar.getInstance().get(Calendar.YEAR)
						&& Integer.parseInt(month) > Calendar.getInstance().get(Calendar.MONTH))) {
			throw new AccountingException("關/開帳月份只可小於本月");
		}

		updateClosed(year, month, status);
		return;
	}

	private void updateClosed(String year, String month, String status) throws Exception {
		TbAccountingClosedPK pk = new TbAccountingClosedPK(year, month);

		TbAccountingClosed accountingClosed = accountingClosedDao.findById(pk);

		if (accountingClosed.getStatus().equals("L")) {
			throw new AccountingException(month + "月份帳目已計算完畢，不可再變動");
		}

		String lastMonth;
		String lastYear;
		if (Integer.parseInt(month) == 1) {
			lastMonth = "12";
			Integer lastYearInteger = ((Integer) Integer.parseInt(year) - 1);
			lastYear = lastYearInteger.toString();
		} else {
			Integer lastMonthInteger = ((Integer) Integer.parseInt(month) - 1);
			lastMonth = lastMonthInteger.toString();
			lastYear = year;
		}

		TbAccountingClosedPK lastPk = new TbAccountingClosedPK(lastYear, lastMonth);
		TbAccountingClosed lastAccountingClosed = accountingClosedDao.findById(lastPk);

		if (Objects.isNull(lastAccountingClosed)) {
		} else if (!lastAccountingClosed.getStatus().equals("L")) {
			throw new AccountingException("尚有月份未完成餘額結算");
		}

		TbAccountingClosedPK updatePk = new TbAccountingClosedPK(year, month);
		TbAccountingClosed updateAccountingClosed = new TbAccountingClosed(updatePk, status,
				accountingClosed.getCreateUser(), accountingClosed.getCreateDate());

		accountingClosedDao.update(updateAccountingClosed);

		return;
	}

}
