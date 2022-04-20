package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.service.AttendanceService;

@RestController
@RequestMapping(value = "/rest/attRecord")
public class RestAttRecordController {
	
	private static final Logger logger = LogManager.getLogger(RestAttRecordController.class);
	
	@Autowired
	private AttendanceService attendanceService;

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見員工的年度差勤資訊。
	 * 透過attendanceService.getYearsAttRecords取得可見員工的年度差勤資料並回傳至前端。
	 */
	@PostMapping("/getRecords")
	public void getRecords(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String authorise = String.valueOf((Integer) req.getSession().getAttribute("Authorise"));
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String depId = (String) req.getSession().getAttribute("depId");
		int year = Integer.parseInt(req.getParameter("year"));
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html,charset=UTF-8");
			JSONArray data = new JSONArray();
			PrintWriter out = resp.getWriter();
			List<Map<String, Object>> getRecords = attendanceService.getYearsAttRecords(nameSelect, depId, authorise, account,
					year);
			for (Map<String, Object> map : getRecords) {
				JSONObject object = new JSONObject();
				for (Map.Entry<String, Object> records : map.entrySet()) {
					object.put(records.getKey(), records.getValue());
				}
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
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得特定員工年度特定差勤明細資訊。
	 * 透過attendanceService.getYearDetail取得特定員工年度特定差勤明細資料並回傳至前端。
	 */
	@PostMapping("/getDetails")
	public void getDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = (String) req.getParameter("name");
		String type = (String) req.getParameter("type");
		
		int year = Integer.parseInt(req.getParameter("year"));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String start, end, status;
		float time;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html,charset=UTF-8");
			JSONArray data = new JSONArray();
			PrintWriter out = resp.getWriter();
			List<TbSchedules> list = attendanceService.getYearDetail(name, type, year);
			Iterator<?> it = list.iterator();
			while (it.hasNext()) {
				TbSchedules record = (TbSchedules) it.next();
				JSONObject object = new JSONObject();
				start = df.format(record.getStartTime());
				end = df.format(record.getEndTime());
				if (record.getAnnualLeaveTimes() == null) {
					time = 0;
				} else {
					time = record.getAnnualLeaveTimes();
				}
				status = "已簽核";
				if (record.getStatus().equals("CREATED")) {
					status = "未簽核";
				}
				object.put("start", start);
				object.put("end", end);
				object.put("status", status);
				object.put("time", time);
				object.put("note", record.getNote());
				data.put(object);
			}
			out.print(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}
}