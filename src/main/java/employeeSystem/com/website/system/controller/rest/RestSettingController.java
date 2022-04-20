package com.yesee.gov.website.controller.rest;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.model.TbPreference;
import com.yesee.gov.website.model.TbPreferenceId;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.PreferenceService;

@RestController
@RequestMapping(value = "/rest/setting")
public class RestSettingController {
	
	private static final Logger logger = LogManager.getLogger(RestSettingController.class);
	
	@Autowired
	private PreferenceService preferenceService;
	
	@Autowired
	private AccountService accountService;
	
	/**
	 * @param req
	 * @param resp
	 * @throws Exception 取得設定的首頁資訊。
	 * 接收前端傳回的資料透過preferenceService.getByUserAndKey取得設定的首頁資料並回傳至前端。
	 */
	@PostMapping("/getPersonalHomepage")
	public void getPersonalHomepageSetting(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String user = (String) req.getSession().getAttribute("Account");
			Integer authorise = (Integer) req.getSession().getAttribute("Authorise");
			Map<String, Object> sidebar;
			sidebar = accountService.getSidebar(authorise);
			String key = "personal_homepage";
			String url = "home.do";
			TbPreference record = preferenceService.getByUserAndKey(user, key);
			if (record != null) {
				if (sidebar.containsValue(record.getValue())) {
					url = record.getValue();
				} else {
					logger.info("the TbPreference is beyond commission");
					preferenceService.del(record);
				}
			}
			logger.info("Homepage url :" + url);
			PrintWriter out = resp.getWriter();
			out.print(url);
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("error : " + e);
		}
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得個人化設定資訊。
	 * 接收Seesion中的Account資料並透過preferenceService.getByUser取得個人化設定資料並回傳至前端。
	 */
	@PostMapping("/getPersonalSetting")
	public Response getPersonalSetting(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String user = (String) req.getSession().getAttribute("Account");
		List<TbPreference> preferences = preferenceService.getByUser(user);
		if (!CollectionUtils.isEmpty(preferences)) {
			return Response.ok(preferences, MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).entity("preferences not found").build();
		}
	}
	/**
	 * @param req
	 * @param resp
	 * @throws Exception 儲存個人化設定資料。 接收前端傳回的設定資料透過preferenceService.save儲存。
	 */
	@PostMapping("/setPersonalHomepage")
	public void homepageSetting(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String user = (String) req.getSession().getAttribute("Account");
		String url = req.getParameter("url");
		String nameSelect = req.getParameter("nameSelect");
		int punchOutRemindHour = Integer.valueOf(req.getParameter("punchOutRemindHour"));
		String remindPunchOut = req.getParameter("remindPunchOut");
		
		logger.info("save TbPreference:");
		logger.info("user : " + user);
		logger.info("key : " + PreferenceService.Key_Personal_Homepage);
		logger.info("value : " + url);
		logger.info("keyOfNameSelect : " + PreferenceService.Key_NameSelect);
		logger.info("nameSelect : " + nameSelect);
		logger.info("keyOfPunchOutRemindTime : " + PreferenceService.Key_PunchOutRemindHour);
		logger.info("punchOutRemindHour : " + punchOutRemindHour);
		logger.info("keyOfremindPunchOut : " + PreferenceService.Key_RemindPunchOut);
		logger.info("remindPunchOut : " + remindPunchOut);
		TbPreference record = preferenceService.getByUserAndKey(user, PreferenceService.Key_Personal_Homepage);
		if (record == null) {
			record = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_Personal_Homepage);
			record.setId(id);
			record.setValue(url);
			preferenceService.save(record);
		} else {
			record.setValue(url);
			preferenceService.save(record);
		}
		if ("EN".equals(nameSelect)) {
			TbPreference nameSelectPreference = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_NameSelect);
			nameSelectPreference.setId(id);
			nameSelectPreference.setValue(nameSelect);
			preferenceService.del(nameSelectPreference);
		} else {
			TbPreference nameSelectPreference = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_NameSelect);
			nameSelectPreference.setId(id);
			nameSelectPreference.setValue(nameSelect);
			preferenceService.save(nameSelectPreference);
		}
		if (punchOutRemindHour < 1) {
			TbPreference PunchOutRemindHourSelect = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_PunchOutRemindHour);
			PunchOutRemindHourSelect.setId(id);
			PunchOutRemindHourSelect.setValue(Integer.toString(punchOutRemindHour));
			preferenceService.del(PunchOutRemindHourSelect);
		} else {
			TbPreference PunchOutRemindHourSelect = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_PunchOutRemindHour);
			PunchOutRemindHourSelect.setId(id);
			PunchOutRemindHourSelect.setValue(Integer.toString(punchOutRemindHour));
			preferenceService.save(PunchOutRemindHourSelect);
		}
		
		if ("remind".equals(remindPunchOut)) {
			TbPreference remindPunchOutSelect = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_RemindPunchOut);
			remindPunchOutSelect.setId(id);
			remindPunchOutSelect.setValue(remindPunchOut);
			preferenceService.del(remindPunchOutSelect);
		} else {
			TbPreference remindPunchOutSelect = new TbPreference();
			TbPreferenceId id = new TbPreferenceId();
			id.setUsername(user);
			id.setConfigKey(PreferenceService.Key_RemindPunchOut);
			remindPunchOutSelect.setId(id);
			remindPunchOutSelect.setValue(remindPunchOut);
			preferenceService.save(remindPunchOutSelect);
		}
		req.getSession().setAttribute("nameSelect", nameSelect);
		req.getSession().setAttribute("punchOutRemindHour", punchOutRemindHour);
		req.getSession().setAttribute("remindPunchOut", remindPunchOut);
		logger.info("Setting Success");
	}
}