package com.yesee.gov.website.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProjectMemberController{
	@RequestMapping("/project_member.do")
	public String jumpJSP(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		return "project_member";
	}
}