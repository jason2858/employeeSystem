package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
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

import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItem;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.service.ProjectItemService;
import com.yesee.gov.website.service.ProjectService;
import com.yesee.gov.website.service.WorkItemService;

@RestController
@RequestMapping(value = "/rest/projectItem")
public class RestProjectItemController {

	private static final Logger logger = LogManager.getLogger(RestProjectItemController.class);

	@Autowired
	private ProjectItemService projectItemService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private WorkItemService workItemService;

	@PostMapping("/getRecords")
	public Response getRecords(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		JSONArray data = new JSONArray();
		try {
			List<TbProjectItem> list = projectItemService.getList(authorise, account);
			if (list.size() != 0) {
				List<Integer> ids = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					if (!list.get(i).getTbProject().getDevStatus().equals("deleted")) {
						ids.add(list.get(i).getItemId());
					}
				}
				Map<Integer, Float> actualHour = workItemService.getActualHour(ids);
				for (int i = 0; i < list.size(); i++) {
					if (!list.get(i).getTbProject().getDevStatus().equals("deleted")) {
						JSONObject object = new JSONObject();
						object.put("id", list.get(i).getItemId());
						object.put("projectId", list.get(i).getTbProject().getId());
						object.put("projectName", list.get(i).getTbProject().getName());
						object.put("projectPM", list.get(i).getTbProject().getPM().getUsername());
						object.put("item", list.get(i).getName());
						if (list.get(i).getHour() != null) {
							object.put("estimateHour", String.valueOf(list.get(i).getHour()));
						} else {
							object.put("estimateHour", "");
						}
						if (actualHour.containsKey(list.get(i).getItemId())) {
							object.put("actualHour", String.valueOf(actualHour.get(list.get(i).getItemId())));
						} else {
							object.put("actualHour", 0);
						}
						object.put("status", list.get(i).getStatus());
						object.put("version", list.get(i).getVersion());
						if ("TW".equals(nameSelect)) {
							object.put("creator", list.get(i).getTbEmployeesByCreator().getChineseName());
							if (list.get(i).getTbEmployeesBySigner() != null) {
								object.put("signer", list.get(i).getTbEmployeesBySigner().getChineseName());
							} else {
								object.put("signer", "");
							}
						} else {
							object.put("creator", list.get(i).getTbEmployeesByCreator().getUsername());
							if (list.get(i).getTbEmployeesBySigner() != null) {
								object.put("signer", list.get(i).getTbEmployeesBySigner().getUsername());
							} else {
								object.put("signer", "");
							}
						}
						data.put(object);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error : " + e);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/getProjects")
	public Response getProjects(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		JSONArray data = new JSONArray();
		List<TbProject> all = projectService.getAllProject();
		if (authorise == 1) {
			for (int i = 0; i < all.size(); i++) {
				if (!all.get(i).getDevStatus().equals("unsign") && !all.get(i).getDevStatus().equals("deleted")) {
					JSONObject object = new JSONObject();
					object.put("id", all.get(i).getId());
					object.put("name", all.get(i).getName());
					object.put("visible", "Y");
					data.put(object);
				}
			}
		} else {
			List<TbProject> project = workItemService.getPersonalProject(account);
			for (int i = 0; i < project.size(); i++) {
				if (!project.get(i).getDevStatus().equals("deleted")) {
					JSONObject object = new JSONObject();
					object.put("id", project.get(i).getId());
					object.put("name", project.get(i).getName());
					object.put("visible", "Y");
					data.put(object);
				}
			}
			for (int i = 0; i < all.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("id", all.get(i).getId());
				object.put("name", all.get(i).getName());
				object.put("visible", "N");
				data.put(object);
			}
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/getDetail")
	public Response getDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer id = Integer.valueOf(req.getParameter("id"));
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		List<TbWorkItem> list = workItemService.getRecordsByItem(id);
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		JSONArray data = new JSONArray();
		for (int i = 0; i < list.size(); i++) {
			JSONObject object = new JSONObject();
			if ("TW".equals(nameSelect)) {
				object.put("name", list.get(i).getTbEmployees().getChineseName());
			} else {
				object.put("name", list.get(i).getTbEmployees().getUsername());
			}
			object.put("date", jd.format(list.get(i).getDate()));
			object.put("hour",String.valueOf(list.get(i).getWorkHr()));
			if(list.get(i).getTbProjectItemSort() == null) {
				object.put("sort", "");
			}else {
				object.put("sort", list.get(i).getTbProjectItemSort().getName());
			}
			if (list.get(i).getNote() != null) {
				object.put("note", list.get(i).getNote());
			} else {
				object.put("note", "");
			}
			data.put(object);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/update")
	public Response update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
		String account = (String) req.getSession().getAttribute("Account");
		String status = req.getParameter("status");
		Integer project = Integer.valueOf(req.getParameter("project"));
		String name = req.getParameter("name");
		String hour = req.getParameter("hour");
		String result = "操作失敗";
		Integer version = 1;
		TbProjectItem object = new TbProjectItem();
		try {
			if (!"CREATED".equals(status)) {
				Integer id = Integer.valueOf(req.getParameter("id"));
				logger.info("id : " + id);
				object = projectItemService.getProjectItemById(id);
				if (object == null) {
					return Response.ok("該項目已不存在", MediaType.APPLICATION_JSON_TYPE).build();
				}
				if (req.getParameter("version") != null) {
					version = Integer.valueOf(req.getParameter("version"));
					if (version != object.getVersion()) {
						return Response.ok("該項目資料已被更新，請再次嘗試", MediaType.APPLICATION_JSON_TYPE).build();
					} else if ("DELETED".equals(status) && "CREATED".equals(object.getStatus())) {
						logger.info("刪除未簽核專案項目");
						projectItemService.delete(object);
						return Response.ok("刪除成功", MediaType.APPLICATION_JSON_TYPE).build();
					}
				} else {
					return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
				}
			}
			if ("CREATED".equals(status) || "UPDATED".equals(status)) {
				object.setTbProject(new TbProject());
				object.getTbProject().setId(project);
				object.setName(name);
				if (hour.length() != 0) {
					object.setHour(Float.valueOf(hour));
				} else {
					object.setHour(null);
				}
			}
			switch (status) {
			case "CREATED":
				logger.info("新增專案項目");
				result = "新增成功";
				object.setTbEmployeesByCreator(new TbEmployees());
				object.getTbEmployeesByCreator().setUsername(account);
				object.setCreatedAt(new Date());
				break;
			case "UPDATED":
				logger.info("更新專案項目");
				result = "更新成功";
				status = "CREATED";
				object.setTbEmployeesByUpdater(new TbEmployees());
				object.getTbEmployeesByUpdater().setUsername(account);
				object.setUpdatedAt(new Date());
				break;
			case "SIGNED":
				logger.info("簽核專案項目");
				result = "簽核成功";
				object.setTbEmployeesBySigner(new TbEmployees());
				object.getTbEmployeesBySigner().setUsername(account);
				object.setSignedAt(new Date());
				break;
			case "DELETED":
				logger.info("刪除專案項目");
				result = "刪除成功";
				object.setTbEmployeesByUpdater(new TbEmployees());
				object.getTbEmployeesByUpdater().setUsername(account);
				object.setUpdatedAt(new Date());
				break;
			}
			if ((authorise == 1 || projectService.findById(project).getPM().getUsername().equals(account))
					&& "CREATED".equals(status)) {
				status = "SIGNED";
				object.setTbEmployeesBySigner(new TbEmployees());
				object.getTbEmployeesBySigner().setUsername(account);
				object.setSignedAt(new Date());
			}
			object.setStatus(status);
			projectItemService.save(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
	}
}