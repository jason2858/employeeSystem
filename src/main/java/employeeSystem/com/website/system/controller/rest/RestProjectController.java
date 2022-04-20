package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectType;
import com.yesee.gov.website.pojo.ProjectVO;
import com.yesee.gov.website.service.DepartmentService;
import com.yesee.gov.website.service.EmployeesService;
import com.yesee.gov.website.service.ProjectService;

@RestController
@RequestMapping(value = "/rest/project", produces = { "application/json;charset=UTF-8" })
public class RestProjectController {
	private static final Logger logger = LogManager.getLogger(RestProjectController.class);

	@Autowired
	private ProjectService projectService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private EmployeesService employeesService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見專案資訊。
	 * 透過projectService.getList取得可見專案資料並回傳至前端。
	 */
	@PostMapping("/getAllProject")
	public String getProjectList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String jsonMsg = null;

		String account = req.getSession().getAttribute("Account").toString();
		String authorise = req.getSession().getAttribute("Authorise").toString();
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");

		try {
			List<ProjectVO> pList = projectService.getList(nameSelect, account, authorise);
			if (!CollectionUtils.isEmpty(pList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pList);
			} 
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得專案類別資訊。
	 * 透過projectService.getTypeList取得專案類別資料並回傳至前端。
	 */
	@PostMapping("/getProjectType")
	public String getTypeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jsonMsg = null;
		try {
			List<TbProjectType> tList = projectService.getTypeList();
			if (!CollectionUtils.isEmpty(tList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tList);
			} else {
				return "fail";
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得已簽核客戶資訊。
	 * 透過projectService.getCustomerList取得已簽核客戶資料並回傳至前端。
	 */
	@PostMapping("/getCustomer")
	public String getEndUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jsonMsg = null;
		try {
			List<TbCustomer> cList = projectService.getCustomerList();
			if (!CollectionUtils.isEmpty(cList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cList);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得部門資訊。
	 * 透過projectService.getDepList取得部門資料並回傳至前端。
	 */
	@PostMapping("/getDepList")
	public String getDepList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String jsonMsg = null;
		try {
			List<TbDepartment> dList = projectService.getDepList();
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

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception
	 * 取得可見在職員工資訊。
	 * 透過projectService.getEmpList或employeesService.getEmployeesByCompany取得可見在職員工資料並回傳至前端。
	 */
	@PostMapping("/getEmpList")
	public Response getEmpList(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String live = req.getParameter("Live");
		TbEmployees tbemployees = null;
		String depId = (String) req.getSession().getAttribute("depId");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		List<TbEmployees> eList = new ArrayList<>();
		String companyId = departmentService.findDepartmentById(depId).getCompanyId();
		JSONArray data = new JSONArray();
		try {
			if (1 == authorise) {
				if ("Y".equals(live)) {
					tbemployees = new TbEmployees();
					tbemployees.setStatus(live);
				}
				eList = projectService.getEmpList(tbemployees);
			} else {
				eList = employeesService.getEmployeesByCompany(companyId, live);
			}
			for (int i = 0; i < eList.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("name", eList.get(i).getUsername());
				object.put("chineseName", eList.get(i).getChineseName());
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
	 * @param pVo
	 * @throws ServletException
	 * @throws IOException
	 * 新增專案資料。
	 * 接收前端傳回的ProjectVO pVo並透過projectService.save儲存並回傳新增結果。
	 */
	@PostMapping("/insertProject")
	public void addNewPro(HttpServletRequest req, HttpServletResponse resp, @RequestBody ProjectVO pVo)
			throws ServletException, IOException {

		try {
			logger.info("addNewPro projectName:" + pVo.getProjectName());
			logger.info("type:" + pVo.getTypeDropdown());
			logger.info("PM:" + pVo.getProjectPm());
			logger.info("SI:" + pVo.getSiDropdown());
			projectService.save(pVo);
			logger.info("addNewPro Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @param pVo
	 * @throws ServletException
	 * @throws IOException
	 * 刪除專案資料。
	 * 接收前端傳回的ProjectVO pVo並透過projectService.delete刪除並回傳刪除結果。
	 */
	@PostMapping("/deleteProject")
	public Response deletePro(HttpServletRequest req, HttpServletResponse resp, @RequestBody ProjectVO pVo)
			throws ServletException, IOException {

		try {
			TbProject object = projectService.checkUpdate(pVo);
			if(object != null) {
				logger.info("deleteProject id:" + pVo.getId());
				projectService.delete(object);
				logger.info("deleteProject Success");
			}else {
				return Response.ok("該專案已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("刪除成功", MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @param pVo
	 * @throws ServletException
	 * @throws IOException
	 * 更新專案資料。
	 * 接收前端傳回的ProjectVO pVo並透過projectService.update儲存並回傳儲存結果。
	 */
	@PostMapping("/updateProject")
	public Response updatePro(HttpServletRequest req, HttpServletResponse resp, @RequestBody ProjectVO pVo)
			throws ServletException, IOException {
		try {
			TbProject object = projectService.checkUpdate(pVo);
			if(object != null) {
				logger.info("updatePro id=" + pVo.getId());
				projectService.update(pVo,object);
				logger.info("updatePro Success");
			}else {
				return Response.ok("該專案已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
			
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("修改成功", MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 簽核專案資料。
	 * 接收前端傳回的專案資料並透過projectService.sign簽核。
	 */
	@PostMapping("/signProject")
	public Response signPro(HttpServletRequest req, HttpServletResponse resp, @RequestBody ProjectVO pVo) 
			throws ServletException, IOException {
		try {
			TbProject object = projectService.checkUpdate(pVo);
			if(object != null) {
				projectService.sign(object);
				logger.info("signPro Success");
			}else {
				return Response.ok("該專案已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("簽核成功", MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @param id
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得特定專案資訊。
	 * 接受前端傳回的id資料透過projectService.findById取得特定專案料並回傳。
	 */
	@PostMapping("/getProject")
	public String getPro(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "id", required = false) Integer id) throws ServletException, IOException {
		String jsonMsg = null;
		try {
			TbProject p = projectService.findById(id);
			ObjectMapper mapper = new ObjectMapper();
			jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);

		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

}