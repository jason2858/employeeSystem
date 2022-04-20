package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.pojo.AnnualLeaveVo;
import com.yesee.gov.website.pojo.ScheduledLeaveVo;
import com.yesee.gov.website.service.AccountService;

@RestController
@RequestMapping(value = "/rest/acc", produces = { "application/json;charset=UTF-8" })
public class RestAccountController {
	private static final Logger logger = LogManager.getLogger(RestAccountController.class);

	@Autowired
	private AccountService accountService;

	/**
	 * @param req
	 * @param resp
	 * @param aVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          儲存年度特休資料。
	 *                          接收前端傳回的AnnualLeaveVo
	 *                          aVo並透過accountService.saveAnnualLeave儲存。
	 */
	@PostMapping("/annualSave")
	public String annSave(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		try {
			logger.info("annualSave empName:" + aVo.getEmpName());
			logger.info("year:" + aVo.getYear());
			logger.info("hours:" + aVo.getEntitledHours());
			accountService.saveAnnualLeave(aVo);
			logger.info("annualSave Success");
		} catch (Exception e) {
			logger.error(e);
			return e.getMessage();
		}
		return "success";
	}

	/**
	 * @param req
	 * @param resp
	 * @param SKDVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          儲存每月排休資料。
	 *                          接收前端傳回的ScheduledLeaveVo
	 *                          SKDVo並透過accountService.saveScheduledLeave儲存。
	 */
	@PostMapping("/skdLeaveSave")
	public String skdLeaveSave(HttpServletRequest req, HttpServletResponse resp, @RequestBody ScheduledLeaveVo SKDVo)
			throws ServletException, IOException {
		try {
			logger.info("skdLeaveSave empName:" + SKDVo.getEmployees());
			accountService.saveScheduledLeave(SKDVo);
			logger.info("skdLeaveSave Success");
		} catch (Exception e) {
			logger.error(e);
			return e.getMessage();
		}
		return "success";
	}

	/**
	 * @param req
	 * @param resp
	 * @param aVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          更新年度特休資料。
	 *                          接收前端傳回的AnnualLeaveVo
	 *                          aVo並透過accountService.updAnnualLeave儲存。
	 */
	@PostMapping("/annualUpd")
	public String annUpd(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		try {
			logger.info("annualUpd empName:" + aVo.getEmpName());
			logger.info("year:" + aVo.getYear());
			logger.info("hours:" + aVo.getEntitledHours());
			accountService.updAnnualLeave(aVo);
			logger.info("annualUpd Success");
		} catch (Exception e) {
			logger.error(e);
			return e.getMessage();
		}
		return "success";
	}

	/**
	 * @param req
	 * @param resp
	 * @param aVo
	 * @throws ServletException
	 * @throws IOException
	 *                          刪除年度特休資料。
	 *                          接收前端傳回的AnnualLeaveVo
	 *                          aVo並透過accountService.deleteAnnualLeave刪除。
	 */
	@PostMapping("/deleteAnnual")
	public void deleteAnn(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		try {
			logger.info("deleteAnnual empName:" + aVo.getEmpName());
			logger.info("year:" + aVo.getYear());
			accountService.deleteAnnualLeave(aVo);
			logger.info("deleteAnnual Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @param companyId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          取得可見的年度特休資訊。
	 *                          透過accountService.getAnnualList取得年度特休資料並回傳至前端。
	 */
	@PostMapping("/getAnnualLeave") // 特休管理
	public String getAnnualLeave(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "companyId", required = false) String companyId)
			throws ServletException, IOException {
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		String jsonMsg = null;
		try {
			List<AnnualLeaveVo> aList = accountService.getAnnualList(nameSelect, companyId, authorise);
			if (!CollectionUtils.isEmpty(aList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(aList);
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
	 * @param name
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          取得可見的現時已排及已休特休時數資訊。
	 *                          透過accountService.getAnnualLeaveOfMonth取得已排及已休特休時數資料並回傳至前端。
	 */
	@PostMapping("/getLeaveCtrList") // 現時特休檢閱
	public String getLeaveCtrList(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "empName", required = false) String name) throws ServletException, IOException {
		String jsonMsg = null;
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		try {
			List<AnnualLeaveVo> aList = accountService.getAnnualLeaveOfMonth(nameSelect, name);
			if (!CollectionUtils.isEmpty(aList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(aList);
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
	 * @param aVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          取得特定員工年度排休資訊。
	 *                          透過accountService.findSchedulesLeaveByEmployees取得特定員工年度排休資料並回傳至前端。
	 */
	@PostMapping("/getSkdLeaveInfo") // 預排特休
	public String getSkdLeaveInfo(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		String jsonMsg = null;
		try {
			String emp = aVo.getAccountName();
			ScheduledLeaveVo vo = accountService.findSchedulesLeaveByEmployees(emp, aVo.getYear());
			if (vo != null) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(vo);
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
	 * @param aVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          取得特定員工年度特休資訊。
	 *                          透過accountService.findAnnualLeaveById取得特定員工年度特休資料並回傳至前端。
	 */
	@PostMapping("/getAnnYearHours")
	public String getAnnYearHours(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		String jsonMsg = null;
		try {
			AnnualLeaveVo ann = accountService.findAnnualLeaveById(aVo);
			ObjectMapper mapper = new ObjectMapper();
			jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ann);
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

	/**
	 * @param req
	 * @param resp
	 * @param aVo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 *                          取得可見的部門資訊。
	 *                          透過accountService.getDepList取得部門資料並回傳至前端。
	 */
	@PostMapping("/getDepListAsBelow")
	public String getDepList(HttpServletRequest req, HttpServletResponse resp, @RequestBody AnnualLeaveVo aVo)
			throws ServletException, IOException {
		String jsonMsg = null;
		try {
			Map<Integer, String> deps = accountService.getDepList(aVo.getEmpName());
			ObjectMapper mapper = new ObjectMapper();
			if (deps.size() > 0) {
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(deps);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

}