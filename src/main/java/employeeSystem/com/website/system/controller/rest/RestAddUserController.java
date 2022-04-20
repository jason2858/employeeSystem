package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.service.DepartmentService;
import com.yesee.gov.website.service.EmployeesService;

@RestController
@RequestMapping(value = "/rest/addUser", produces = { "application/json;charset=UTF-8" })
public class RestAddUserController {
	private static final Logger logger = LogManager.getLogger(RestAddUserController.class);

	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private DepartmentService departmentService;

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見的員工資訊。
	 * 透過employeesService.getEmployeesInfo取得員工資料並回傳至前端。
	 */
	@PostMapping("/getEmployeesList")
	public void getEmployeesList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONObject data = new JSONObject();
		resp.setCharacterEncoding("UTF-8");
		String authorise = req.getParameter("authorise");
		String companyId = req.getParameter("companyId");
		String account = (String) req.getSession().getAttribute("Account");
		PrintWriter out = resp.getWriter();
		try {
			out.print("[");
			Map<String, Object> empInfo = new HashMap<>();
			if ("1".equals(authorise)) {
				empInfo = employeesService.getEmployeesInfo(account, "admin");
			} else {
				empInfo = employeesService.getEmployeesInfo(account, companyId);
			}
			logger.info("Employees' Count : " + empInfo.get("count"));
			for (int i = 0; i < (Integer) empInfo.get("count"); i++) {
				if (i != 0) {
					out.print(",");
				}
				data.put("username", empInfo.get(i + "username"));
				data.put("chineseName", empInfo.get(i + "chineseName"));
				data.put("groupId", empInfo.get(i + "groupId"));
				data.put("status", empInfo.get(i + "status"));
				data.put("createdAt", empInfo.get(i + "createdAt"));
				data.put("updatedAt", empInfo.get(i + "updatedAt"));
				data.put("department", empInfo.get(i + "department"));
				data.put("onBoardDate", empInfo.get(i + "onBoardDate"));
				out.print(data);
			}
			out.print("]");
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
	 * 儲存員工資訊。
	 * 接收前端傳回的員工資料並透過employeesService.addUser儲存。
	 */
	@PostMapping("/addUser")
	public void addUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String empEnName = req.getParameter("empEnName");
			String empChName = req.getParameter("empChName");
			String dep = req.getParameter("dep");
			String onBoardDate = req.getParameter("onBoardDate");
			logger.info("New employee's empEnName : " + empEnName);
			logger.info("New employee's empChName : " + empChName);
			logger.info("New employee's dep : " + dep);
			logger.info("New employee's onBoardDate : " + onBoardDate);
			employeesService.addUser(empEnName, empChName, dep, onBoardDate, "4");
			logger.info("addUser Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 更新員工資訊。
	 * 接收前端傳回的員工資料並透過employeesService.editUser儲存。
	 */
	@PostMapping("/edit")
	public void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		String dep = req.getParameter("dep");
		String groupId = req.getParameter("groupId");
		String status = req.getParameter("status");
		logger.info("Edit employee's name : " + name);
		logger.info("Edit employee's dep : " + dep);
		logger.info("Edit employee's groupId : " + groupId);
		logger.info("Edit employee's status : " + status);
		try {
			employeesService.editUser(name, dep, groupId, status);
			logger.info("EditUser Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 刪除員工資訊。
	 * 接收前端傳回的員工姓名並透過employeesService.deleteUse刪除。
	 */
	@PostMapping("/del")
	public void del(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		logger.info("Delete employee's name : " + name);
		try {
			employeesService.deleteUser(name);
			logger.info("DeleteUser Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見的部門資訊。
	 * 依權限不同透過departmentService.getRecords或departmentService.getListByCompanyId取得部門資料並回傳至前端。
	 */
	@PostMapping("/getDepList")
	public String getDepList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jsonMsg = null;
		String authorise = req.getParameter("authorise");
		String companyId = req.getParameter("companyId");
		List<TbDepartment> dList = new ArrayList<>();
		try {
			if ("1".equals(authorise)) {
				dList = departmentService.getRecords();
			} else {
				dList = departmentService.getListByCompanyId(companyId);
			}
			if (!CollectionUtils.isEmpty(dList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dList);
			} else {
				return "fail";
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}
}