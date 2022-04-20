package com.yesee.gov.website.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.VTbSchedules;
import com.yesee.gov.website.pojo.Attendance;
import com.yesee.gov.website.pojo.DeleteAttendanceInfo;
import com.yesee.gov.website.pojo.InsertAttendanceInfo;
import com.yesee.gov.website.pojo.UpdateAttendanceInfo;
import com.yesee.gov.website.service.AttendanceService;
import com.yesee.gov.website.service.EmployeesService;

@RestController
@RequestMapping(value = "/rest/attendance", produces = "application/json;charset=UTF-8")
public class RestAttendanceController {
	private static final Logger logger = LogManager.getLogger(RestAttendanceController.class);

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private EmployeesService employeesService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見的差勤資訊。
	 *                   透過attendanceService.getAttendanceInfo取得差勤資料並回傳至前端。
	 */
	@PostMapping(value = "/records")
	public Response getEvenets(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		int authorise = Integer.parseInt(req.getSession().getAttribute("Authorise").toString());
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String acc = req.getParameter("account");
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		List<Attendance> list = attendanceService.getAttendanceInfo(nameSelect, authorise, acc, startDate, endDate);
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			data.put(list.get(i));
		}
		return Response.ok(data, MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param insertAttendanceInfo
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 儲存差勤資料。 接收前端傳回的InsertAttendanceInfo
	 *                   insertAttendanceInfo並透過attendanceService.transAttInfoToObjAndInsert儲存。
	 *                   儲存完後透過attendanceService.sendEmail發送信件。
	 *                   回傳儲存結果(success/fail)至前端。
	 */
	@PostMapping(value = "/insert")
	public Response insertAttendanceData(@RequestBody InsertAttendanceInfo insertAttendanceInfo, HttpServletRequest req,
			HttpServletResponse resp) throws Exception {

		String result = "success";

		String account = req.getSession().getAttribute("Account").toString();

		int id = 0;
		// log input data
		logger.info("User who would like to insert attendance info:" + account);
		logger.info("Inserting Data:");
		logger.info("startTime: " + insertAttendanceInfo.getStartTime());
		logger.info("endTime: " + insertAttendanceInfo.getEndTime());
		logger.info("note: " + insertAttendanceInfo.getNote());
		logger.info("type: " + insertAttendanceInfo.getType());
		logger.info("annualLeaveTimes: " + insertAttendanceInfo.getAnnualLeaveTimes());
		logger.info("deputy: " + insertAttendanceInfo.getDeputy());

		try {
			id = attendanceService.transAttInfoToObjAndInsert(insertAttendanceInfo, account);
		} catch (Exception e) {
			logger.error(e);
			result = "fail";
		}

		// 寄信
//		if (id != 0) {
//			int i = id;
//			Thread thread = new Thread(new Runnable() {
//				public void run() {
//					try {
//						attendanceService.sendEmail(i);
//					} catch (Exception e) {
//						logger.error(e);
//					}
//				}
//			});
//			thread.start();
//		}
//
		// use objectmapper
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			logger.error(e);
			result = "fail";
		}
		logger.info("insert Result" + result);
		return Response.ok(result).build();
	}

	/**
	 * @param updateAttendanceInfo
	 * @param req
	 * @param resp
	 * @return 更新差勤資料。 接收前端傳回的UpdateAttendanceInfo
	 *         updateAttendanceInfo並透過attendanceService.transAttInfoToObjAndUpdate儲存。
	 *         回傳儲存結果(success/fail)至前端。
	 */
	@PostMapping(value = "/update")
	public Response updateAttendanceData(@RequestBody UpdateAttendanceInfo updateAttendanceInfo, HttpServletRequest req,
			HttpServletResponse resp) {

		String result = "success";

		String account = req.getSession().getAttribute("Account").toString();
		logger.info("User who would like to update attendance info:" + account);
		logger.info("Updating Data:");
		logger.info("startTime: " + updateAttendanceInfo.getStartTime());
		logger.info("endTime: " + updateAttendanceInfo.getEndTime());
		logger.info("note: " + updateAttendanceInfo.getNote());
		logger.info("status: " + updateAttendanceInfo.getStatus());
		logger.info("type: " + updateAttendanceInfo.getType());
		logger.info("annualLeaveTimes: " + updateAttendanceInfo.getAnnualLeaveTimes());
		logger.info("deputy: " + updateAttendanceInfo.getDeputy());

		try {
			attendanceService.transAttInfoToObjAndUpdate(updateAttendanceInfo, account);
		} catch (Exception e1) {
			logger.error(e1);
			result = "fail";
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			logger.error(e);
			result = "fail";
		}

		logger.info("Updating Result: " + result);

		return Response.ok(result).build();
	}

	/**
	 * @param deleteAttendanceInfo
	 * @param req
	 * @param resp
	 * @return 刪除差勤資料。 接收前端傳回的DeleteAttendanceInfo
	 *         deleteAttendanceInfo並透過attendanceService.setAttInfoToObjAndDelete刪除。
	 *         回傳刪除結果(success/fail)至前端。
	 */
	@PostMapping(value = "/delete")
	public Response deleteAttendanceData(@RequestBody DeleteAttendanceInfo deleteAttendanceInfo, HttpServletRequest req,
			HttpServletResponse resp) {

		String result = "success";

		String account = req.getSession().getAttribute("Account").toString();
		logger.info("user:" + account);

		String id = deleteAttendanceInfo.getId();
		logger.info("Delete attendance Info id:" + id);

		try {
			attendanceService.setAttInfoToObjAndDelete(id);
		} catch (Exception e1) {
			logger.error(e1);
			result = "fail";
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			logger.error(e);
			result = "fail";
		}

		logger.info("delete result:" + result);
		return Response.ok(result).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return 取得可見在職員工資訊。 透過attendanceService.getEmployees取得在職員工資料並回傳至前端。
	 */
	@PostMapping(value = "/nameFilter")
	public Response nameFilter(HttpServletRequest req, HttpServletResponse resp) {
		String account = req.getSession().getAttribute("Account").toString();
		String live = req.getParameter("Live");
		TbEmployees tbemployees = null;
		JSONArray data = new JSONArray();
		try {
			if ("Y".equals(live)) {
				tbemployees = new TbEmployees();
				tbemployees.setStatus(live);
			}
			List<TbEmployees> subEmpList = attendanceService.getEmployees(account, tbemployees);
			for (int i = 0; i < subEmpList.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("name", subEmpList.get(i).getUsername());
				object.put("chineseName", subEmpList.get(i).getChineseName());
				data.put(object);
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("database problem").build();
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 檢查差勤資訊是否被簽核或刪除。
	 *                   透過attendanceService.check取得差勤是否被簽核或駁回並回傳結果(1/0)至前端。
	 */
	@PostMapping(value = "/check")
	public Response check(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String id = req.getParameter("id");
		return Response.ok(attendanceService.check(id)).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return 取得可見且不包含自己的員工資訊。 透過announceService.getRecords取得可見且不包含自己的員工資料並回傳至前端。
	 */
	@PostMapping(value = "/getDeputy")
	public Response getDeputy(HttpServletRequest req, HttpServletResponse resp) {
		String live = req.getParameter("Live");
		String account = req.getSession().getAttribute("Account").toString();
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		String companyId = (String) req.getSession().getAttribute("companyId");
		List<TbEmployees> list = null;
		TbEmployees tbemployees = null;
		JSONArray data = new JSONArray();
		try {
			if ("Y".equals(live)) {
				tbemployees = new TbEmployees();
				tbemployees.setStatus(live);
			}
			if (authorise == 1) {
				list = employeesService.getEmployees(tbemployees);
			} else {
				list = employeesService.getEmployeesByCompany(companyId, live);
			}
			for (int i = 0; i < list.size(); i++) {
				JSONObject object = new JSONObject();
				if (!list.get(i).getUsername().equals(account)) {
					object.put("name", list.get(i).getUsername());
					object.put("chineseName", list.get(i).getChineseName());
					data.put(object);
				}
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("database problem").build();
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得加班總時數、已補休時數、剩餘可補休時數。
	 *                   透過attendanceService.getAvailableTimeList 取得剩餘可補休時數清單至前端。
	 */
	@PostMapping(value = "/getAvailableTime")
	public Response getAvailableTime(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getSession().getAttribute("Account").toString();
		String acc = null;
		if(req.getParameter("account")!=null) {
			acc = req.getParameter("account");
		}
		JSONArray data = new JSONArray();
		if (acc != null && acc != account) {
			List<VTbSchedules> list2 = attendanceService.getAvailableTimeList(acc);
			for (int i = 0; i < list2.size(); i++) {
				data.put(list2.get(i));
			}
		} else {
			List<VTbSchedules> list = attendanceService.getAvailableTimeList(account);

			for (int i = 0; i < list.size(); i++) {
				data.put(list.get(i));
			}
		}
		return Response.ok(data, MediaType.APPLICATION_JSON_TYPE).build();
	}
}
