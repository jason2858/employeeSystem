package com.yesee.gov.website.controller.accounting;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounting")
public class ASignController {

	@RequestMapping("/sign.do")
	public String jumpAccountSignJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_sign";
	}
	
	@RequestMapping("/aSign.do")
	public String jumpAccountASignJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//配合前端頁面跳轉至accounting_sign_check.jsp
//		return "/accounting_aSign";
		return "/accounting_sign_check";
	}
}
