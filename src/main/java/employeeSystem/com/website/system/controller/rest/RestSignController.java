package com.yesee.gov.website.controller.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.pojo.SignMultiCheckVo;
import com.yesee.gov.website.pojo.SignMultiMakeUpVO;
import com.yesee.gov.website.service.AttendanceService;
import com.yesee.gov.website.service.PunchService;

@RestController
@RequestMapping(value = "/rest/sign")
public class RestSignController {

	@Autowired
	private PunchService punchService;

	@Autowired
	private AttendanceService attendanceService;
	private static final Logger logger = LogManager.getLogger(RestSignController.class);

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見未簽核補打卡資訊。
	 *                   接受前端相關資料透過punchService.getUnsignedMakeUpRecords取得可見未簽核補打卡資料後並回傳。
	 */
	@PostMapping("/getUnsignPunchRecords")
	public Response getUnsignPunchRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String depId = (String) req.getSession().getAttribute("depId");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		List<Map<String, Object>> record = punchService.getUnsignedMakeUpRecords(nameSelect, depId,
				String.valueOf(authorise), account);
		JSONArray data = new JSONArray();
		for (Map<String, Object> map : record) {
			JSONObject object = new JSONObject();
			for (Map.Entry<String, Object> records : map.entrySet()) {
				object.put(records.getKey(), records.getValue());
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 檢查補打卡狀態並簽核或刪除。
	 *                   接受前端相關資料透過punchService.checkMakeUpAndUpdate簽核或刪除後回傳結果至前端。
	 */
	@PostMapping(value = "/checkMakeUpAndUpdate")
	public Response checkMakeUpAndUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String id = req.getParameter("id");
		String status = req.getParameter("status");
		String reason = req.getParameter("reason");
		return Response
				.ok(punchService.checkMakeUpAndUpdate(account, id, status, reason), MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 一次檢查多個補打卡狀態並簽核。
	 *                   POST傳來各需要檢查的資料，然後分開交給punchService.checkMakeUpAndUpdate/
	 */

	@PostMapping(value = "/multiCheckMakeUpUpdate")
	public Response multiCheckMakeUpUpdate(HttpServletRequest req, HttpServletResponse resp,
			@RequestBody ArrayList<SignMultiMakeUpVO> multiCheck) throws Exception {

		String account = (String) req.getSession().getAttribute("Account");
		Map<String, Integer> checkResultLMap = new HashMap<String, Integer>();

		for (SignMultiMakeUpVO eachCheck : (ArrayList<SignMultiMakeUpVO>) multiCheck) {
			String id = eachCheck.getId();
			String status = eachCheck.getStatus();
			String reason = "";
			int checkResult = punchService.checkMakeUpAndUpdate(account, id, status, reason);
			checkResultLMap.put(id, checkResult);
		}

		return Response.ok(checkResultLMap, MediaType.APPLICATION_JSON_TYPE).build();

	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見未簽核差勤資訊。
	 *                   接受前端相關資料透過attendanceService.getUnsignedAttRecords取得可見未簽核差勤資料後並回傳。
	 */
	@PostMapping("/getUnsignAttRecords")
	public Response getUnsignAttRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String depId = (String) req.getSession().getAttribute("depId");
		List<Map<String, Object>> getRecords = attendanceService.getUnsignedAttRecords(nameSelect, depId,
				String.valueOf(authorise), account);
		JSONArray data = new JSONArray();
		for (Map<String, Object> map : getRecords) {
			JSONObject object = new JSONObject();
			for (Map.Entry<String, Object> records : map.entrySet()) {
				object.put(records.getKey(), records.getValue());
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 檢查差勤狀態並簽核或刪除。
	 *                   接受前端相關資料透過attendanceService.checkScheduleAndUpdate簽核或刪除後回傳結果至前端。
	 */
	@PostMapping(value = "/checkScheduleAndUpdate")
	public Response checkScheduleAndUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String id = req.getParameter("id");
		String updatedAt = req.getParameter("updatedAt");
		String status = req.getParameter("status");
		String reason = req.getParameter("reason");
		return Response.ok(attendanceService.checkScheduleAndUpdate(account, id, updatedAt, status, reason),
				MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 一次檢查多個差勤狀態並簽核。
	 *                   POST傳來各需要檢查的資料，然後分開交給attendanceService.checkScheduleAndUpdate/
	 */

	@PostMapping(value = "/multiCheckScheduleUpdata")
	public Response multiCheckScheduleUpdata(HttpServletRequest req, HttpServletResponse resp,
			@RequestBody ArrayList<SignMultiCheckVo> multiCheck) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		Map<String, Integer> checkResultLMap = new HashMap<String, Integer>();

		for (SignMultiCheckVo eachCheck : (ArrayList<SignMultiCheckVo>) multiCheck) {
			String id = eachCheck.getId();
			String updatedAt = eachCheck.getUpdateAt();
			if (updatedAt == null) {
				updatedAt = "null";
			}
			String status = eachCheck.getStatus();
			String reason = "";
			int checkResult = attendanceService.checkScheduleAndUpdate(account, id, updatedAt, status, reason);
			checkResultLMap.put(id, checkResult);
		}

		return Response.ok(checkResultLMap, MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見已簽核/已駁回補打卡資訊。
	 *                   接受前端相關資料透過punchService.getMakeUpRecords取得可見已簽核/已駁回補打卡資料後並回傳。
	 */
	@PostMapping("/getNotCreatedPunchRecords")
	public Response getNotCreatedPunchRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String status = req.getParameter("status");
		String depId = (String) req.getSession().getAttribute("depId");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String startDate = req.getParameter("year") + "-" + req.getParameter("month") + "-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startday = sdf.parse(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startday);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		String endDate = sdf.format(cal.getTime());
		List<Map<String, Object>> record = punchService.getMakeUpRecords(nameSelect, status, depId,
				String.valueOf(authorise), account, startDate, endDate);
		JSONArray data = new JSONArray();
		for (Map<String, Object> map : record) {
			JSONObject object = new JSONObject();
			for (Map.Entry<String, Object> records : map.entrySet()) {
				object.put(records.getKey(), records.getValue());
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得可見已簽核/已駁回差勤資訊。
	 *                   接受前端相關資料透過attendanceService.getAttRecords取得可見已簽核/已駁回差勤資料後並回傳。
	 */
	@PostMapping("/getNotCreatedAttRecords")
	public Response getNotCreatedAttRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String status = req.getParameter("status");
		String depId = (String) req.getSession().getAttribute("depId");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String startDate = req.getParameter("year") + "-" + req.getParameter("month") + "-01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startday = sdf.parse(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(startday);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		String endDate = sdf.format(cal.getTime());
		List<Map<String, Object>> getRecords = attendanceService.getAttRecords(nameSelect, status, depId,
				String.valueOf(authorise), account, startDate, endDate);
		JSONArray data = new JSONArray();
		for (Map<String, Object> map : getRecords) {
			JSONObject object = new JSONObject();
			for (Map.Entry<String, Object> records : map.entrySet()) {
				object.put(records.getKey(), records.getValue());
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
}