package com.yesee.gov.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController{
	@RequestMapping("/home.do")
	public String jumpJSP(HttpServletRequest req,
			HttpServletResponse resp) throws Exception{
		    return "home";
	}
}