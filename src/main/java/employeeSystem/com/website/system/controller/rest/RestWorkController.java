package com.yesee.gov.website.controller.rest;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.DepartmentService;
import com.yesee.gov.website.service.ProjectService;
import com.yesee.gov.website.service.WorkItemService;

@RestController
@RequestMapping(value = "/rest/work")
public class RestWorkController {

	private static final Logger logger = LogManager.getLogger(RestWorkController.class);

	@Autowired
	private WorkItemService workItemService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ProjectService projectService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得部門資訊。
	 * 根據權限透過projectService.getDepList或epartmentService.getListByCompanyId取得部門資料並回傳至前端。
	 */
	@PostMapping("/getDepartment")
	public Response getDepartment(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String depId = (String) req.getSession().getAttribute("depId");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		List<TbDepartment> list = new ArrayList<>();
		if (authorise == 1) {
			list = departmentService.getRecords();
		} else {
			String companyId = departmentService.findDepartmentById(depId).getCompanyId();
			list = departmentService.getListByCompanyId(companyId);
		}
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			map.put(String.valueOf(list.get(i).getId()), list.get(i).getName());
		}
		JSONArray data = new JSONArray();
		if (authorise == 3) {
			Set<String> set = departmentService.getChildDepartments(depId);
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				JSONObject object = new JSONObject();
				String id = it.next();
				object.put("id", id);
				object.put("name", map.get(id));
				data.put(object);
			}
		} else {
			for (String key : map.keySet()) {
				JSONObject object = new JSONObject();
				object.put("id", key);
				object.put("name", map.get(key));
				data.put(object);
			}
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得部門員工資訊。
	 * 接受前端傳回部門透過accountService.getByDepartmentSet取得部門員工資料後回傳至前端。
	 */
	@PostMapping("/getEmployees")
	public Response getEmployees(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String department = req.getParameter("department");
		logger.info("workRecord department list : " + department);
		Set<String> dep = new HashSet<String>();
		for (int i = 0; i < department.split(",").length; i++) {
			dep.add(department.split(",")[i]);
		}
		List<TbEmployees> record = accountService.getByDepartmentSet(dep);
		JSONArray data = new JSONArray();
		for (int i = 0; i < record.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("name", record.get(i).getUsername());
			object.put("chineseName", record.get(i).getChineseName());
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得工時統計資訊。
	 * 接受前端員工資料透過workItemService.getRecordsByList計算後統計工時資料並回傳至前端。
	 */
	@PostMapping("/getRecords")
	public Response getRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String employee = req.getParameter("employee");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		logger.info("get total work time :");
		logger.info("employees :" + employee);
		logger.info("between " + start + " to " + end);
		List<String> employees = new ArrayList<String>();
		for (int i = 0; i < employee.split(",").length; i++) {
			employees.add(employee.split(",")[i]);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(start);
		Date endDate = sdf.parse(end);
		List<TbWorkItem> list = workItemService.getRecordsByList(employees, startDate, endDate);
		Map<Integer, Float> hour = new HashMap<Integer, Float>();
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			if (hour.containsKey(list.get(i).getProId())) {
				hour.put(list.get(i).getProId(), hour.get(list.get(i).getProId()) + list.get(i).getWorkHr());
			} else {
				hour.put(list.get(i).getProId(), list.get(i).getWorkHr());
			}
		}
		List<TbProject> all = projectService.getAllProject();
		Map<Integer, Object> project = new HashMap<Integer, Object>();
		for (int i = 0; i < all.size(); i++) {
			project.put(all.get(i).getId(), all.get(i).getName());
		}
		for (Integer key : hour.keySet()) {
			JSONObject object = new JSONObject();
			object.put("id", key);
			object.put("project", project.get(key));
			object.put("hour", hour.get(key));
			object.put("hd", hour.get(key) / 8);
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得特定專案工時資料。
	 * 接受前端資料透過workItemService.getRecordsByListAndProjectId取得特定專案工時資料並回傳至前端。
	 */
	@PostMapping("/getDetail")
	public Response getDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String id = req.getParameter("id");
		String employee = req.getParameter("employee");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		logger.info("get work item detail :");
		logger.info("project_Id :" + id);
		logger.info("employees :" + employee);
		logger.info("between " + start + " to " + end);
		List<String> employees = new ArrayList<String>();
		for (int i = 0; i < employee.split(",").length; i++) {
			employees.add(employee.split(",")[i]);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(start);
		Date endDate = sdf.parse(end);
		List<TbWorkItem> list = workItemService.getRecordsByListAndProjectId(Integer.valueOf(id), employees, startDate,
				endDate);
		Map<String, Float> hour = new HashMap<String, Float>();
		JSONArray data = new JSONArray();

		if ("TW".equals(nameSelect)) {
			for (int i = 0; i < list.size(); i++) {
				if (hour.containsKey(list.get(i).getTbEmployees().getChineseName())) {
					hour.put(list.get(i).getTbEmployees().getChineseName(),
							hour.get(list.get(i).getTbEmployees().getChineseName()) + list.get(i).getWorkHr());
				} else {
					hour.put(list.get(i).getTbEmployees().getChineseName(), list.get(i).getWorkHr());
				}
			}
		} else {
			for (int i = 0; i < list.size(); i++) {
				if (hour.containsKey(list.get(i).getTbEmployees().getUsername())) {
					hour.put(list.get(i).getTbEmployees().getUsername(),
							hour.get(list.get(i).getTbEmployees().getUsername()) + list.get(i).getWorkHr());
				} else {
					hour.put(list.get(i).getTbEmployees().getUsername(), list.get(i).getWorkHr());
				}
			}
		}

		for (String key : hour.keySet()) {
			JSONObject object = new JSONObject();
			object.put("name", key);
			object.put("hour", hour.get(key));
			object.put("hd", hour.get(key) / 8);
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
}