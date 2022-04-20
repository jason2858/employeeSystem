package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yesee.gov.website.service.DirectDeleteService;

@Controller
@RequestMapping(value = "/rest/directDelete")
public class RestDirectDeleteController {

	private static final Logger logger = LogManager.getLogger(RestDirectDeleteController.class);

	@Autowired
	private DirectDeleteService directDeleteService;

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 將URL中的密碼存至session並轉跳至/directSign.do。
	 */
	@RequestMapping("/code/*")
	public void sign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getRequestURI();
		code = code.replace("/rest/directDelete/code/", "");
		logger.info("Email sign attendance :");
		logger.info("HashCode : " + code);
		req.getSession().setAttribute("hashcode", code);
		resp.sendRedirect("/directDelete.do?code=" + code);
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception
	 * 簽核差勤資訊。
	 * 取得session中的code並透過directRejectService.getDirectSignMap取得簽核結果並回傳。
	 */
	@RequestMapping("/getStatus")
	public void status(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String code = req.getParameter("code");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		PrintWriter out = resp.getWriter();
		Map<String, Object> map = directDeleteService.getDirectDeleteMap(code);
		out.println(map.get("result"));
		out.flush();
		out.close();
	}
}