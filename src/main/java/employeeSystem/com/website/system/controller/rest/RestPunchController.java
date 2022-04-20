package com.yesee.gov.website.controller.rest;

import java.io.IOException;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbPunchRecords;
import com.yesee.gov.website.pojo.PunchRecordVO;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.PunchService;
import com.yesee.gov.website.util.Config;

@RestController
@RequestMapping(value = "/rest/punch")
public class RestPunchController {
	
	private static final Logger logger = LogManager.getLogger(RestPunchController.class);
	
	@Autowired
	private PunchService punchService;
	
	@Autowired
	private AccountService accountService;
	
	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得當日最後打卡狀態。
	 * 接收session中Account資料並透過punchService.getPunchStatus判斷打卡狀態並回傳結果(absence/in/out)至前端。
	 */
	@PostMapping("/getPunchStatus")
	public void getPunchStatus(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String user = (String) req.getSession().getAttribute("Account");
		try {
			PrintWriter out = resp.getWriter();
			String status = "absence";
			List<TbPunchRecords> record = punchService.getPunchStatus(user);
			if (!record.isEmpty()) {
				if (record.get(0).getType().equals("out") || record.get(0).getType().equals("makeupout")) {
					status = "out";
				} else {
					status = "in";
				}
			}
			out.print(status);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得特定月份已簽核補打卡次數。
	 * 接收前端傳回的使用者及日期資料並透過punchService.getMakeUpCount計算已簽核補打卡次數並回傳。
	 */
	@PostMapping("/getMakeUpCount")
	public void getMakeUpCount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String user = req.getParameter("account");
		String startDate = req.getParameter("date").substring(0, 8) + "01";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startdate = sdf.parse(startDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(startdate);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, 1);
			String endDate = sdf.format(cal.getTime());
			PrintWriter out = resp.getWriter();
			List<TbPunchRecords> list = punchService.getMakeUpCount(user, startDate, endDate);
			out.print(list.size());
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見員工資訊。
	 * 接受前端傳回相關資料透過accountService.getSubordinateList取得可見員工資料並回傳。
	 */
	@PostMapping("/getEmployees")
	public void getEmployees(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
			String depId = (String) req.getSession().getAttribute("depId");
			List<TbEmployees> record = accountService.getSubordinateList(String.valueOf(authorise), depId,null);
			JSONArray data = new JSONArray();
			resp.setCharacterEncoding("UTF-8");
			PrintWriter out = resp.getWriter();
			for (int i = 0; i < record.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("name", record.get(i).getUsername());
				object.put("chineseName", record.get(i).getChineseName());
				data.put(object);
			}
			out.print(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param date
	 * @return
	 * 將傳入的date加上星期並轉為string回傳。
	 */
	public String getWeek(Date date) {
		SimpleDateFormat week = new SimpleDateFormat("u");
		switch (week.format(date)) {
		case "1":
			return "(一)";
		case "2":
			return "(二)";
		case "3":
			return "(三)";
		case "4":
			return "(四)";
		case "5":
			return "(五)";
		case "6":
			return "(六)";
		case "7":
			return "(日)";
		default:
			return "";
		}
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得打卡相關資訊。
	 * 取得打卡相關資訊並回傳至前端。
	 */
	@SuppressWarnings("deprecation")
	@PostMapping("/getPunchRecords")
	public void getRecords(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String user = req.getParameter("id");
			String month = req.getParameter("month");
			if(month.length() < 2) {
				month = "0" + month;
			}
			String startDate = req.getParameter("year") + "-" + month + "-01";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
			Date startday = sdf.parse(startDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(startday);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, 1);
			String endDate = sdf.format(cal.getTime());
			Date date = new Date();
			cal.add(Calendar.DATE, -1);
			String holidayEnd = sdf.format(cal.getTime());
			if (startday.getYear() == date.getYear() && startday.getMonth() == date.getMonth()) {
				holidayEnd = sdf.format(date);
			}
			Map<String, Object> attendance = punchService.getAttendance(user, startDate, endDate);
			Map<String, Object> holiday = punchService.getHoliday(startDate, holidayEnd);
			List<TbPunchRecords> list = punchService.getRecords(user, startDate, endDate);
			Iterator<TbPunchRecords> it = list.iterator();
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html,charset=UTF-8");
			PrintWriter out = resp.getWriter();
			String day, weekDay, time, why, type, detail;
			int count = 0, outcount = 0;
			boolean mui = false, muo = false;
			PunchRecordVO result = new PunchRecordVO();
			Gson toJSON = new GsonBuilder().serializeNulls().create();
			out.print("[");
			
			Config config = Config.getInstance();
			int size = Integer.parseInt(config.getValue("legalposition_size"));
			int range = Integer.parseInt(config.getValue("legalposition_range"));
			float latitude = 0;
			float longitude = 0;
			
			while (it.hasNext()) {
				TbPunchRecords record = it.next();
				if (count == 0) {
					date = record.getPunchTime();
				}
				Date d = record.getPunchTime();
				weekDay = getWeek(d);
				day = sdf.format(d) + " " + weekDay;
				time = tf.format(d);
				if (count == 0) {
					result.setPunchDate(day);
				}
				if (date.getDate() != d.getDate()) {
					mui = false;
					muo = false;
					outcount = 0;
					out.print(toJSON.toJson(result));
					result = new PunchRecordVO();
					result.setPunchDate(day);
					date = d;
					out.print(",");
				}
				why = record.getPositionInfo();
				String note = null;
				
				float latitudeRec = record.getLatitude() == null ? 0 : record.getLatitude().floatValue();
				float longitudeRec = record.getLongitude() == null ? 0 : record.getLongitude().floatValue();
				
				for (int i = 1; i <= size; i++) {
					String location = config.getValue("legalposition_" + i);
					String[] Stringsplit = location.split(",");
					latitude = Float.valueOf(Stringsplit[0]);
					longitude = Float.valueOf(Stringsplit[1]);
					if (Math.sqrt(Math.pow(latitudeRec - latitude, 2) + Math.pow(longitudeRec - longitude, 2))
							* 100000 <= range) {// 100公尺內的駐點或總部
						note = config.getObject("legalposition_" + i).getNote();
					}
				}
				
				if (why != null) {
					if (why.equals("1")) {
						why = "無位置資訊";
					} else if (why.equals("2")) {
						why = "系統位置異常";
					} else if (why.equals("3")) {
						why = "駐點";
					} else if (why.equals("4")) {
						why = "出差";
					} else if (why.equals("5")) {
						why = "其他";
					} else if (why.equals("6")) {
						why = "外出";
					}
				} else {
					if (note != null) {
						why = note;
					} else {
						why = "無異常";
					}
				}
				if (record.getType().equals("makeupin") && record.getTbEmployeesBySigner() == null) {
					why = "補上班打卡(未簽核)";
				}
				if (record.getType().equals("makeupout") && record.getTbEmployeesBySigner() == null) {
					why = "補下班打卡(未簽核)";
				}
				if ((record.getType().equals("makeupin") || record.getType().equals("makeupout"))
						&& record.getTbEmployeesBySigner() != null) {
					why = "補打卡(已簽核)";
				}
				// 以已簽核補打卡資料為優先,並將mui設定為true表示有補上班打卡紀錄
				if (record.getType().equals("makeupin") && record.getTbEmployeesBySigner() != null) {
					mui = true;
					result.setInTime(time);
					result.setInLatitude(record.getLatitude());
					result.setInLongitude(record.getLongitude());
					result.setInWhy(why);
					result.setInNote(record.getNote());
				}
				// 以已簽核補打卡資料為優先,並將mui設定為true表示有補下班打卡紀錄
				if (record.getType().equals("makeupout") && record.getTbEmployeesBySigner() != null) {
					muo = true;
					result.setOutTime(time);
					result.setOutLatitude(record.getLatitude());
					result.setOutLongitude(record.getLongitude());
					result.setOutWhy(why);
					result.setOutNote(record.getNote());
				}
				//寫入未簽核補上班打卡紀錄
				if (record.getType().equals("makeupin") && record.getTbEmployeesBySigner() == null) {
					result.setMuiId(record.getId());
					result.setMuiTime(time);
					result.setMuiLatitude(record.getLatitude());
					result.setMuiLongitude(record.getLongitude());
					result.setMuiWhy(why);
					result.setMuiNote(record.getNote());
				}
				//若無已簽核補上班打卡紀錄,寫入此筆上班打卡紀錄
				else if (!mui && record.getType().equals("in") || record.getType().equals("makeupin")) {
					result.setInTime(time);
					result.setInLatitude(record.getLatitude());
					result.setInLongitude(record.getLongitude());
					result.setInWhy(why);
					result.setInNote(record.getNote());
				} 
				//寫入未簽核補下班打卡紀錄
				else if (record.getType().equals("makeupout") && record.getTbEmployeesBySigner() == null) {
					result.setMuoId(record.getId());
					result.setMuoTime(time);
					result.setMuoLatitude(record.getLatitude());
					result.setMuoLongitude(record.getLongitude());
					result.setMuoWhy(why);
					result.setMuoNote(record.getNote());
				}
				//若無已簽核補下班打卡紀錄,寫入此筆下班打卡紀錄
				else if (!muo && record.getType().equals("out") || record.getType().equals("makeupout")) {
					if (outcount == 0) {
						result.setOutTime(time);
						result.setOutLatitude(record.getLatitude());
						result.setOutLongitude(record.getLongitude());
						result.setOutWhy(why);
						result.setOutNote(record.getNote());
					}
					outcount++;
				}
				//寫入當天節慶
				if (holiday.get(sdf.format(d) + "name") != null) {
					result.setHolidayType(String.valueOf(holiday.get(sdf.format(d) + "type")));
					result.setHoliday((String) holiday.get(sdf.format(d) + "name"));
					holiday.remove(sdf.format(d) + "name");
					holiday.remove(sdf.format(d) + "type");
				}
				//寫入當天差勤
				if (attendance.get(sdf.format(d)) != null) {
					detail = (String) attendance.get(sdf.format(d));
					type = detail.substring(detail.length() - 5, detail.length() - 2);
					result.setAttendanceType(type);
					result.setAttendance(detail);
					attendance.remove(sdf.format(d));
				}
				count++;
			}
			if (result.getPunchDate() != null) {
				out.print(toJSON.toJson(result));
			}
			
			//補上剩餘節慶及差勤資料
			if (holiday.size() != 0 || attendance.size() != 0) {
				String holidayYear = holidayEnd.substring(0, 5);
				String holidayMonth = holidayEnd.substring(5, 8);
				int holidayDay = Integer.parseInt(holidayEnd.substring(8, 10));
				for (int i = 1; i <= holidayDay; i++) {
					String holidayDate = holidayYear + holidayMonth;
					if (i < 10) {
						holidayDate += "0";
					}
					holidayDate += i;
					if (holiday.get(holidayDate + "name") != null || attendance.get(holidayDate) != null) {
						result = new PunchRecordVO();
						if (count != 0)
							out.print(",");
						Date holidayTrans = sdf.parse(holidayDate);
						weekDay = getWeek(holidayTrans);
						result.setPunchDate(holidayDate + " " + weekDay);
						if (holiday.get(holidayDate + "name") != null) {
							result.setHoliday((String) holiday.get(holidayDate + "name"));
							result.setHolidayType(String.valueOf(holiday.get(holidayDate + "type")));
						}
						if (attendance.get(holidayDate) != null) {
							detail = (String) attendance.get(holidayDate);
							type = detail.substring(detail.length() - 5, detail.length() - 2);
							result.setAttendanceType(type);
							result.setAttendance(detail);
							attendance.remove(holidayDate);
						}
						out.print(toJSON.toJson(result));
						count++;
					}
				}
			}
			out.print("]");
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 新增打卡資料。
	 * 接收前端傳回打卡資料並透過punchService.punch儲存並回傳結果至前端。
	 */
	@PostMapping("/punch")
	public void punch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String user = (String) req.getSession().getAttribute("Account");
		Date time = new Date();
		String type = req.getParameter("type");
		String latitude = req.getParameter("latitude");
		String longitude = req.getParameter("longitude");
		String why = req.getParameter("why");
		String reason = req.getParameter("reason");
		
		logger.info("punch, user:" + user + " , type:" + type + ", why:" + why + ", reason:" + reason);
		
		TbPunchRecords record = new TbPunchRecords();
		record.setId(null);
		record.setPunchTime(time);
		record.setTbEmployeesByUser(new TbEmployees());
		record.getTbEmployeesByUser().setUsername(user);
		record.setType(type);
		if (reason.length() < 1) {
			record.setNote(null);
		} else {
			record.setNote(reason);
		}
		record.setTbEmployeesBySigner(null);
		record.setCreatedAt(time);
		record.setUpdatedAt(time);
		record.setStatus("CREATED");
		if (latitude.equals("null")) {
			record.setLatitude(null);
		} else {
			record.setLatitude(new BigDecimal(latitude));
		}
		if (longitude.equals("null")) {
			record.setLongitude(null);
		} else {
			record.setLongitude(new BigDecimal(longitude));
		}
		if (why.length() < 1) {
			record.setPositionInfo(null);
		} else {
			record.setPositionInfo(why);
		}
		try {
			int result = punchService.punch(record);
			out.print(result);
			logger.info("punch result: " + result);
		} catch (Exception e) {
			logger.error(e);
		}
		out.flush();
		out.close();
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 新增補打卡資料。
	 * 接收前端傳回補打卡資料並透過punchService.makeUp儲存並回傳結果至前端。
	 */
	@PostMapping("/makeup")
	public void makeUp(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String user = req.getParameter("account");
		String makeupDate = req.getParameter("date");
		String punchtime = makeupDate + " " + req.getParameter("phour") + ":" + req.getParameter("pmin") + ":00";
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String type = req.getParameter("type");
		String note = req.getParameter("note");
		String latitude = req.getParameter("latitude");
		String longitude = req.getParameter("longitude");
		
		logger.info("makeupPunch, user:" + user + ", type:" + type + ", date:" + punchtime + ", note:" + note);
		
		try {
			TbPunchRecords record = new TbPunchRecords();
			record.setId(null);
			record.setPunchTime(sdFormat.parse(punchtime));
			record.setTbEmployeesByUser(new TbEmployees());
			record.getTbEmployeesByUser().setUsername(user);
			record.setType(type);
			record.setNote(note);
			record.setTbEmployeesBySigner(null);
			record.setCreatedAt(date);
			record.setUpdatedAt(date);
			record.setStatus("CREATED");
			if (latitude == null) {
				record.setLatitude(null);
			} else {
				record.setLatitude(new BigDecimal(latitude));
			}
			if (longitude == null) {
				record.setLongitude(null);
			} else {
				record.setLongitude(new BigDecimal(longitude));
			}
			try {
				int result = punchService.makeUp(record);
				out.print(result);
				logger.info("makeup punch result: " + result);
			} catch (Exception e) {
				logger.error(e);
			}
			out.flush();
			out.close();
		} catch (ParseException e) {
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 刪除未簽核補打卡資料。
	 * 接收前端傳回的補打卡資料並透過punchService.del刪除補打卡資料並回傳刪除結果至前端。
	 */
	@PostMapping("/del")
	public void del(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id = Long.parseLong(req.getParameter("id"));
		PrintWriter out = resp.getWriter();
		logger.info("Delete punch record's id : " + id);
		try {
			out.print(punchService.del(id));
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}
}