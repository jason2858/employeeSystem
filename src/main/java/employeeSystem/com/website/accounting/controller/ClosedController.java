package com.yesee.gov.website.controller.accounting;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounting")
public class ClosedController {

	@GetMapping("/closed.do")
	public String jumpClosedJSP(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		return "/accounting_closed";
	}

}
