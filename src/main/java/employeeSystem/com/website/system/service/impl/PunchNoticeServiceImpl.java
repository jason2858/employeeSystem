package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.yesee.gov.website.dao.HolidayEventDao;
import com.yesee.gov.website.dao.VPunchHistoryDao;
import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.exception.SystemOutException;
import com.yesee.gov.website.model.TbHolidayEvent;
import com.yesee.gov.website.model.VTbPunchHistory;
import com.yesee.gov.website.service.PunchNoticeService;
import com.yesee.gov.website.service.SendMailService;
import com.yesee.gov.website.util.Config;
import com.yesee.gov.website.util.DateUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("punchNoticeService")
public class PunchNoticeServiceImpl implements PunchNoticeService {

	@Autowired
	private VPunchHistoryDao vPunchHistoryDao;

	@Autowired
	private HolidayEventDao holidayEventDao;

	@Autowired
	private SendMailService sendMailDao;

	@Override
	public String getPunchList(HttpServletRequest req, HttpServletResponse resp) throws SystemOutException, Exception {

		String startDate = req.getParameter("start_date");
		String endDate = req.getParameter("end_date");

		return getPunchList(startDate, endDate);
	}

	private String getPunchList(String startDate, String endDate) throws Exception {
		JSONObject result = new JSONObject();

		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");

		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {

			throw new SystemOutException("欄位不能為空值");
		}

		startDate = startDate.replace("-", "");
		endDate = endDate.replace("-", "");

		List<VTbPunchHistory> list = vPunchHistoryDao.getPunchList(startDate, endDate);

		if (CollectionUtils.isEmpty(list)) {
			throw new SystemOutException("查無資料");
		}

		List<TbHolidayEvent> holidayList = holidayEventDao.findHolidayByDates(startDate, endDate);

		List<String> holiday = new ArrayList<String>();

		for (int i = 0; i < holidayList.size(); i++) {

			if (holidayList.get(i).getHolidayType() == 2) {
				holiday.add(sdFormat.format(holidayList.get(i).getStartDate()));
			}
		}

		JSONArray objectList = new JSONArray();

		for (int i = 0; i < list.size(); i++) {
			String weekday = new SimpleDateFormat("u").format(DateUtil.StringToDate(list.get(i).getPunchDate()));

			// 篩選是否為六日
			if (weekday.equals("6") || weekday.equals("7")) {

				// 篩選是否為補班日
				if (holiday.contains(list.get(i).getPunchDate())) {
					JSONObject object = new JSONObject();
					object.put("account", list.get(i).getAccount());
					object.put("name", list.get(i).getName());
					object.put("punchDate", list.get(i).getPunchDate());
					object.put("inStatus", list.get(i).getInStatus());
					object.put("outStatus", list.get(i).getOutStatus());
					object.put("schedules", list.get(i).getSchedules());
					object.put("schedulesTime", list.get(i).getSchedulesTime());
					objectList.add(object);
				}
			} else {
				JSONObject object = new JSONObject();
				object.put("account", list.get(i).getAccount());
				object.put("name", list.get(i).getName());
				object.put("punchDate", list.get(i).getPunchDate());
				object.put("inStatus", list.get(i).getInStatus());
				object.put("outStatus", list.get(i).getOutStatus());
				object.put("schedules", list.get(i).getSchedules());
				object.put("schedulesTime", list.get(i).getSchedulesTime());
				objectList.add(object);
			}
		}
		result.put("punchList", StringUtils.isEmpty(objectList) ? null : objectList);
		return result.toString();

	}

	@Override
	public String sendNoticeMail(HttpServletRequest req, HttpServletResponse resp,JSONObject accountInfo)
			throws SystemOutException, Exception {
		List<Map<String, String>> account = (List<Map<String, String>>) accountInfo.get("notice");
		return sendNoticeMail(account);
	}

	private String sendNoticeMail(List<Map<String,String>> accountList) throws Exception {
		
		String today =  new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		try {
			Config config = Config.getInstance();

			for (int i = 0; i < accountList.size(); i++) {

				String account = accountList.get(i).get("account");
				String date = accountList.get(i).get("notice_date");
				if (config.getValue("mail_attendanceapply_send").equals("Y")) {

					StringBuilder builder = new StringBuilder();

					builder.append(date);
					builder.append(" 尚未完成打卡，");
					builder.append("\n請盡快完成補打卡程序 ! ");
					builder.append("\n");
					builder.append("\n");
					builder.append("管理部敬上");

					// 信箱
					String recipient = account + "@yesee.com.tw";
					// 主旨
					String subject = date+" 補打卡通知";
					// 內容
					String base = builder.toString();
					
					sendMailDao.sendEmail(recipient, subject, base);
				}
			}
		} catch (Exception e) {
			throw new SystemOutException("送信失敗");
		}
		return "送信成功";
	}
}
