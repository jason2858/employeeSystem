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

import com.yesee.gov.website.service.DirectRejectService;

@Controller
@RequestMapping(value = "/rest/directReject")
public class RestDirectRejectController {
	
	private static final Logger logger = LogManager.getLogger(RestDirectRejectController.class);

	@Autowired
	private DirectRejectService directRejectService;

	
	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 * 將URL中的密碼存至session並轉跳至/directReject.do。
	 */
	@RequestMapping("/code/*")
	public void sign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getRequestURI();
		String URI = "/directReject.do";
		code = code.replace("/rest/directReject/code/", "");
		logger.info("Email sign attendance :");
		logger.info("HashCode : " + code);
		req.getSession().setAttribute("hashcode", code);
		resp.sendRedirect(URI);
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception
	 * 駁回差勤資訊。
	 * 取得session中的code並透過directRejectService.getDirectRejectMap取得駁回結果並回傳。
	 */
	@RequestMapping("/getStatus")
	public void getStatus(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String code = (String) req.getSession().getAttribute("hashcode");
		String reason = req.getParameter("reason");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html,charset=UTF-8");
		PrintWriter out = resp.getWriter();
		Map<String, Object> map = directRejectService.getDirectRejectMap(code, reason);
		out.println(map.get("result"));
		out.flush();
		out.close();
	}
}