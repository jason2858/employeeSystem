package com.yesee.gov.website.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.service.CalendarService;

@RestController
@RequestMapping(value = "/rest/calendar", produces = "application/json;charset=UTF-8")
public class RestCalendarController {
	private static final Logger logger = LogManager.getLogger(RestCalendarController.class);

	@Autowired
	private CalendarService calendarService;

	public void setAccountService(CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 *         取得行事曆上可見的員工差勤及節慶資訊。
	 *         透過calendarService.chgCallendarInfoToEvent取得可見的員工差勤及節慶資料並回傳至前端。
	 */
	@GetMapping(value = "/records")
	public String getEvenets(HttpServletRequest req, HttpServletResponse resp) {
		String jsonMsg = null;
		String account = req.getSession().getAttribute("Account").toString();
		String nameSelect = (String) req.getSession().getAttribute("nameSelect");
		int authorise = Integer.parseInt(req.getSession().getAttribute("Authorise").toString());
		String depId = (String) req.getSession().getAttribute("depId");
		try {
			jsonMsg = calendarService.chgCallendarInfoToEvent(nameSelect, authorise, account, depId);
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}

	/**
	 * @param req
	 * @param resp
	 *             更新節慶資訊。
	 *             透過calendarService.uploadHoliday取得並儲存節慶資料。
	 */
	@GetMapping(value = "/uploadHoliday")
	public void uploadHoliday(HttpServletRequest req, HttpServletResponse resp) {
		try {
			calendarService.uploadHoliday();
			logger.info("uploadHoliday Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
