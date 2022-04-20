package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.yesee.gov.website.model.TbCompany;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.CompanyService;
import com.yesee.gov.website.service.DepartmentService;
import com.yesee.gov.website.service.EmployeesService;

import net.sf.json.JSONException;

@RestController
@RequestMapping(value = "/rest/department")
public class RestDepartmentController {

	private static final Logger logger = LogManager.getLogger(RestDepartmentController.class);

	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeesService employeesService;
	@Autowired
	private AccountService accountService;

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 取得部門相關資訊。
	 * 透過departmentService.getSum取得部門人數資料。
	 * 透過departmentService.getRecords取得部門資料。
	 * 以特定格式將資料回傳至前端。
	 */
	@PostMapping("/getRecords")
	public void getRecords(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String data = "{ \"class\": \"go.TreeModel\"," + "\"nodeDataArray\": [";
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		PrintWriter out = resp.getWriter();
		try {
			int count = 0;
			String id;
			Map<String, Integer> map = departmentService.getSum();
			List<TbDepartment> list = departmentService.getRecords();
			Iterator<TbDepartment> it = list.iterator();
			while (it.hasNext()) {
				if (count != 0) {
					data += ",";
				}
				TbDepartment record = it.next();
				id = Integer.toString(record.getId());
				data += "{\"key\":" + record.getId();
				data += ",\"name\":\"" + record.getName() + "\"";
				if (map.get(id) != null) {
					data += ",\"sum\":" + map.get(id);
				} else {
					data += ",\"sum\":" + "0";
				}
				if (record.getManager() != null) {
					if ("TW".equals(nameSelect)) {
						data += ",\"manager\":\"" + record.getManager().getChineseName() + "\"";
					} else {
						data += ",\"manager\":\"" + record.getManager().getUsername() + "\"";
					}
				} else {
					data += ",\"manager\":\"" + "無" + "\"";
				}
				if (record.getParentId() != null) {
					data += ",\"parent\":" + record.getParentId();
				} else {
					data += ",\"parent\":-" + record.getCompanyId();
				}
				if (record.getDescription() != null) {
					data += ",\"comments\":\"" + record.getDescription() + "\"";
				}
				data += "}";
				count++;
			}
			// set companys
			List<TbCompany> companys = companyService.getList();
			for (int i = 0; i < companys.size(); i++) {
				data += ",";
				data += "{\"key\":-" + companys.get(i).getId();
				data += ",\"name\":\"" + companys.get(i).getName() + "\"";
				data += "}";
			}

			data += "]";
			data += "}";
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
	 * 取得部門資訊。
	 * 透過departmentService.getRecords取得部門資料並回傳至前端。
	 */
	@PostMapping("/getParents")
	public void getParents(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			// List<TbCompany> clist = companyService.getList();
			List<TbDepartment> list = departmentService.getRecords();
			Iterator<TbDepartment> it = list.iterator();
			JSONArray data = new JSONArray();
			while (it.hasNext()) {
				TbDepartment record = it.next();
				JSONObject object = new JSONObject();
				object.put("id", record.getId());
				object.put("parent", record.getName());
				object.put("companyId", record.getCompanyId());
				data.put(object);
			}
			data = sortJsonArray(data);
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
	 * 取得公司資訊。
	 * 透過companyService.getList取得部門資料並回傳至前端。
	 */
	@PostMapping("/getCompanys")
	public void getCompanys(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			List<TbCompany> list = companyService.getList();
			Iterator<TbCompany> it = list.iterator();
			JSONArray data = new JSONArray();
			while (it.hasNext()) {
				TbCompany record = it.next();
				JSONObject object = new JSONObject();
				object.put("id", record.getId());
				object.put("name", record.getName());
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
	 * @param data
	 * @return
	 * 將傳入的JSONArray data以公司編排。
	 */
	private JSONArray sortJsonArray(JSONArray data) {

		JSONArray sortedJsonArray = new JSONArray();

		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < data.length(); i++) {
			jsonValues.add(data.getJSONObject(i));
		}

		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private static final String KEY_NAME = "companyId";

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = new String();
				String valB = new String();

				try {
					valA = (String) a.get(KEY_NAME);
					valB = (String) b.get(KEY_NAME);
				} catch (JSONException e) {
					logger.error(e);
				}

				return valA.compareTo(valB);
			}
		});

		for (int i = 0; i < jsonValues.size(); i++) {
			sortedJsonArray.put(jsonValues.get(i));
		}
		return sortedJsonArray;

	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得特定部門在職員工資訊。
	 * 透過mployeesService.getManagers取得特定部門在職員工資料並回傳至前端。
	 */
	@PostMapping("/getManagers")
	public Response getManagers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		String live = req.getParameter("Live");
		TbEmployees tbemployees = null;
		List<String> ids = new ArrayList<>();
		ids.add(id);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		PrintWriter out = resp.getWriter();
		JSONArray data = new JSONArray();
		try {
			if ("Y".equals(live)) {
				tbemployees = new TbEmployees();
				tbemployees.setStatus(live);
			}
			List<TbEmployees> list = employeesService.getManagers(ids, tbemployees);
			Iterator<TbEmployees> it = list.iterator();
			while (it.hasNext()) {
				TbEmployees record = it.next();
				JSONObject object = new JSONObject();
				object.put("name", record.getUsername());
				object.put("chineseName", record.getChineseName());
				data.put(object);
			}
			out.print(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("database problem").build();
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 新增部門資訊。
	 * 接收前端傳回的部門資訊儲存至本地並透過departmentService.updateDepartment儲存。
	 */
	@PostMapping("/addDepartment")
	public void addDepartment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String name = req.getParameter("name");
			String manager = req.getParameter("manager");
			String parent = req.getParameter("parent");
			String description = req.getParameter("note");
			TbDepartment record = new TbDepartment();
			record.setName(name);
			if (!parent.isEmpty()) {
				String companyId = departmentService.findDepartmentById(parent).getCompanyId();
				record.setCompanyId(companyId);
				logger.info("companyId: " + companyId);
				record.setParentId(Integer.valueOf(parent));
			} else {
				record.setParentId(null);
			}
			if (!manager.isEmpty()) {
				record.setManager(new TbEmployees());
				record.getManager().setUsername(manager);
			} else {
				record.setManager(null);
			}
			if (description.length() < 1) {
				record.setDescription(null);
			} else {
				record.setDescription(description);
			}
			logger.info("addDepartment name:" + name);
			logger.info("manager: " + manager);
			logger.info("parent: " + parent);
			logger.info("description: " + description);
			departmentService.updateDepartment(record, "add");
			logger.info("addDepartment Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 修改部門資訊。
	 * 接收前端傳回的部門資訊儲存至本地並透過departmentService.updateDepartment儲存。
	 */
	@PostMapping("/editDepartment")
	public void editDepartment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			Integer id = Integer.valueOf(req.getParameter("id"));
			String name = req.getParameter("name");
			String newManager = req.getParameter("newManager");
			String parent = req.getParameter("parent");
			String description = req.getParameter("note");
			String manager = req.getParameter("manager");
			TbDepartment record = new TbDepartment();
			record.setId(id);
			record.setName(name);
			if (!parent.isEmpty()) {
				String companyId = departmentService.findDepartmentById(parent).getCompanyId();
				record.setCompanyId(companyId);
				logger.info("companyId: " + companyId);
				record.setParentId(Integer.valueOf(parent));
			} else {
				record.setParentId(null);
			}

			if (!newManager.isEmpty()) {
				record.setManager(new TbEmployees());
				record.getManager().setUsername(newManager);
			} else if (!manager.isEmpty() && !"none".equals(manager)) {
				record.setManager(new TbEmployees());
				record.getManager().setUsername(manager);
			} else {
				record.setManager(null);
			}

			if (description.length() < 1) {
				record.setDescription(null);
			} else {
				record.setDescription(description);
			}
			logger.info("editDepartment name:" + name);
			logger.info("manager: " + manager);
			logger.info("newManager: " + newManager);
			logger.info("parent: " + parent);
			logger.info("description: " + description);
			departmentService.updateDepartment(record, manager);
			logger.info("editDepartment Success");
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
	 * 刪除部門資訊。
	 * 接收前端傳回的部門資訊儲存至本地並透過departmentService.delDepartment刪除。
	 */
	@PostMapping("/delDepartment")
	public Response delDepartment(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String id = req.getParameter("id");
			TbDepartment record = departmentService.findDepartmentById(id);
			Set<String> depSet = new HashSet<String>();
			depSet.add(id);
			List<TbEmployees> emps = accountService.getByDepartmentSet(depSet);
			if (emps.size() > 0) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("請先調動該部門員工").build();
			}
			logger.info("delDepartment, id=" + id);
			departmentService.delDepartment(record);
			logger.info("delDepartment Success");
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("OK", MediaType.APPLICATION_JSON_TYPE).build();
	}
}