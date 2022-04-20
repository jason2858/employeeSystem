package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbPunchRecords;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.PreferenceService;
import com.yesee.gov.website.service.PunchService;
import com.yesee.gov.website.service.StatusService;

@Service("statusService")
public class StatusServiceImpl implements StatusService {

	@Autowired
	private PunchService punchService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private DepartmentDao departmentDao;

	@Override
	public List<Map<String, Object>> getRecords(String nameSelect, String authorise, String depId, TbEmployees entity)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
		List<TbDepartment> depList = departmentDao.getList();
		Map<String, Object> department = new HashMap<>();
		for (int i = 0; i < depList.size(); i++) {
			department.put(String.valueOf(depList.get(i).getId()), depList.get(i).getName());
		}
		List<TbEmployees> employees = accountService.getSubordinateList(authorise, depId, entity);
		String date;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < employees.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			if ("TW".equals(nameSelect)) {
				map.put("username", employees.get(i).getChineseName());
			} else {
				map.put("username", employees.get(i).getUsername());
			}
			map.put("dep", department.get(employees.get(i).getDepartmentId()));
			List<TbPunchRecords> record = punchService.getPunchStatus(employees.get(i).getUsername());
			if (!record.isEmpty()) {
				date = df.format(record.get(0).getPunchTime());
				map.put("type", record.get(0).getType());
				map.put("time", date);
			} else {
				map.put("type", "absence");
				map.put("time", "empty");
			}
			result.add(map);
		}
		return result;
	}
}