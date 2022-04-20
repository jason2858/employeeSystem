package com.yesee.gov.website.controller.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectMember;
import com.yesee.gov.website.pojo.ProjectVO;
import com.yesee.gov.website.service.ProjectMemberService;
import com.yesee.gov.website.service.ProjectService;

import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/projectMember")
public class RestProjectMemberController {

	private static final Logger logger = LogManager.getLogger(RestProjectMemberController.class);

	@Autowired
	private ProjectMemberService projectMemberService;

	@Autowired
	private ProjectService projectService;

	@PostMapping("/getRecords")
	public Response getRecords(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String authorise = req.getSession().getAttribute("Authorise").toString();
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String projectId = req.getParameter("projectId");
		JSONArray data = new JSONArray();
		try {
			List<Integer> projects = new ArrayList<Integer>();
			if("all".equals(projectId)) {
				List<ProjectVO> projectList = projectService.getList(nameSelect, account, authorise);
				projects = projectList.stream().filter(e -> !"unsign".equals(e.getDevStatus()))
						.filter(e -> !"deleted".equals(e.getDevStatus())).map(m -> Integer.parseInt(m.getId())).collect(Collectors.toList());
			}else {
				projects.add(Integer.parseInt(projectId));
			}
			List<TbProjectMember> members = projectMemberService.getList(projects);
			for (int i = 0; i < members.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("id", members.get(i).getId());
				object.put("projectId", members.get(i).getTbProject().getId());
				object.put("projectName", members.get(i).getTbProject().getName());
				if("TW".equals(nameSelect)) {
					object.put("member", members.get(i).getTbEmployees().getChineseName());
				}else {
					object.put("member", members.get(i).getTbEmployees().getUsername());
				}
				data.put(object);
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/getProjects")
	public Response getProjects(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		String authorise = req.getSession().getAttribute("Authorise").toString();
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		JSONArray data = new JSONArray();
		try {
			List<ProjectVO> list = projectService.getList(nameSelect, account, authorise);
			for (int i = 0; i < list.size(); i++) {
				if (!"unsign".equals(list.get(i).getDevStatus()) && !"deleted".equals(list.get(i).getDevStatus())) {
					JSONObject object = new JSONObject();
					object.put("id", list.get(i).getId());
					object.put("name", list.get(i).getProjectName());
					data.put(object);
				}
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/getEmployee")
	public Response getEmployee(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer projectId = Integer.valueOf(req.getParameter("projectId"));
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		JSONArray data = new JSONArray();
		try {
			List<TbEmployees> list = projectMemberService.getNotMemberEmployees(projectId);
			logger.info("get employee not in projectId = " + projectId);
			logger.info("count = " + list.size());
			for (int i = 0; i < list.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("value", list.get(i).getUsername());
				if("TW".equals(nameSelect)) {
					object.put("chineseName", list.get(i).getChineseName());
				}else {
					object.put("name", list.get(i).getUsername());
				}
				data.put(object);
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	@PostMapping("/addMember")
	public void addMember(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer projectId = Integer.valueOf(req.getParameter("projectId"));
		String member = req.getParameter("member");
		TbProjectMember object = new TbProjectMember();
		for (int i = 0; i < member.split(",").length; i++) {
			object = new TbProjectMember();
			object.setTbProject(new TbProject());
			object.getTbProject().setId(projectId);
			logger.info("save TbPorjectMember projectId = " + projectId + " and member = " + member.split(",")[i]);
			object.setTbEmployees(new TbEmployees());
			object.getTbEmployees().setUsername(member.split(",")[i]);
			object.setCreatedAt(new Date());
			projectMemberService.save(object);
		}
	}
	
	@PostMapping("/delMember")
	public void delMember(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer id = Integer.valueOf(req.getParameter("id"));
		projectMemberService.del(id);
	}
}