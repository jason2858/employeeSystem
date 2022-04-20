package com.yesee.gov.website.controller.rest;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.service.PunchNoticeService;

@RestController
@RequestMapping(value = "/rest", produces = "application/json;charset=UTF-8")
public class RestPunchNoticeController {

	private static final Logger logger = LogManager.getLogger(RestPunchNoticeController.class);

	@Autowired
	public PunchNoticeService punchNoticeService;

	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得員工打卡狀態。 透過vPunchHistoryDao.getList取得員工打卡狀態資料並回傳至前端。
	 */
	@GetMapping(value = "/punchNotice")
	public Response getPunchNotice(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		
		return Response.ok(punchNoticeService.getPunchList(req, resp), MediaType.APPLICATION_JSON_TYPE).build();

	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 通知員工補打卡。
	 */
	@PostMapping(value = "/punchNotice/sendMail")
	public Response sendPunchNotice(HttpServletRequest req, HttpServletResponse resp,@RequestBody JSONObject accountInfo) throws Exception {
		
		return Response.ok(punchNoticeService.sendNoticeMail(req, resp,accountInfo), MediaType.APPLICATION_JSON_TYPE).build();

	}

}
