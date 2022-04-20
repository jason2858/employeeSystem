package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.service.StatusService;

@RestController
@RequestMapping(value = "/rest/status")
public class RestStatusController {

	private static final Logger logger = LogManager.getLogger(RestStatusController.class);

	@Autowired
	private StatusService statusService;
	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得當日可見員工打卡狀態。
	 * 接受前端相關資料透過statusService.getRecords取得當日可見員工打卡資料後並回傳。
	 */
	@PostMapping("/getRecords")
	public void getRecords(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String live = req.getParameter("Live");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String authorise = String.valueOf((Integer) req.getSession().getAttribute("Authorise"));
		String depId = (String) req.getSession().getAttribute("depId");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		TbEmployees tbemployees = null;
		if ("Y".equals(live)) {
			tbemployees = new TbEmployees();
			tbemployees.setStatus(live);
		}
		JSONArray data = new JSONArray();
		PrintWriter out = resp.getWriter();
		try {
			List<Map<String, Object>> getRecords = statusService.getRecords(nameSelect, authorise, depId, tbemployees);
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
}