package com.yesee.gov.website.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PunchNoticeController {

	@RequestMapping("/punchNotice.do")
	public String jumpPunchNoticeJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/punchNotice";
	}
}