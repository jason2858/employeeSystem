package com.yesee.gov.website.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.yesee.gov.website.util.Config;

@Controller
public class PunchManagementController {

	@RequestMapping("/punch_management.do")
	public ModelAndView jumpJSP(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView view = new ModelAndView("punch_management");
		Config config = Config.getInstance();
		int size = Integer.parseInt(config.getValue("legalposition_size"));
		view.addObject("legalposition_range", config.getValue("legalposition_range"));
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i = 1; i <= size; i++) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			String location = config.getValue("legalposition_" + i);
			String[] Stringsplit = location.split(",");
			map.put("latitude", Stringsplit[0]);
			map.put("longitude", Stringsplit[1]);
			data.add(map);
		}
		
		String POSITION_DETAIL_LIST = new Gson().toJson(data);
		view.addObject("POSITION_DETAIL_LIST", POSITION_DETAIL_LIST);
		view.addObject("workitem_punchcheck", config.getValue("workitem_punchcheck"));
		return view;
	}

}