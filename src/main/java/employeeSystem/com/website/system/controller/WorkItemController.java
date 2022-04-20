package com.yesee.gov.website.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WorkItemController{
	@RequestMapping("/working_item.do")
	public String jumpWorkingItem(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		return "working_item";
	}
	
	@RequestMapping("/working_record.do")
	public ModelAndView jumpWorkingRecord(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView view = new ModelAndView("working_record");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.getTime();
		cal.add(Calendar.MONTH,-1);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));
		Date start = cal.getTime();
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		Date end = cal.getTime();
		view.addObject("start", sdf.format(start));
		view.addObject("end", sdf.format(end));
		return view;
	}
}