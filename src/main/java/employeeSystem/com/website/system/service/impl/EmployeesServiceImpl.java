package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.service.EmployeesService;
import com.yesee.gov.website.util.DateUtil;

@Service("employeesService")
public class EmployeesServiceImpl implements EmployeesService {
	private static final Logger logger = LogManager.getLogger(EmployeesServiceImpl.class);

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private EmployeesDao employeesDao;

	@Override
	public Map<String, Object> getEmployeesInfo(String account, String companyId) throws Exception {
		// 取得員工中英文設定
		Map<String, Object> mapOb = new HashMap<String, Object>();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TbEmployees> empList = new ArrayList<>();
		if ("admin".equals(companyId)) {
			empList = employeesDao.getList(null);
		} else {
			empList = this.getEmployeesByCompany(companyId, null);
		}

		for (int i = 0; i < empList.size(); i++) {
			mapOb.put(i + "chineseName", empList.get(i).getChineseName());
			mapOb.put(i + "username", empList.get(i).getUsername());
			mapOb.put(i + "groupId", empList.get(i).getGroupId());
			mapOb.put(i + "status", empList.get(i).getStatus());
			if (empList.get(i).getCreatedAt() != null) {
				mapOb.put(i + "createdAt", sd.format(empList.get(i).getCreatedAt()));
			} else {
				mapOb.put(i + "createdAt", "");
			}
			if (empList.get(i).getUpdatedAt() != null) {
				mapOb.put(i + "updatedAt", sd.format(empList.get(i).getUpdatedAt()));
			} else {
				mapOb.put(i + "updatedAt", "");
			}
			mapOb.put(i + "department", empList.get(i).getDepartmentId());
			if (empList.get(i).getOnBoardDate() != null) {
				mapOb.put(i + "onBoardDate", sd.format(empList.get(i).getOnBoardDate()));
			} else {
				mapOb.put(i + "onBoardDate", "");
			}
		}
		mapOb.put("count", empList.size());

		mapOb.replaceAll((k, v) -> {// depID<=>depName
			if (k.contains("department") && v != null) {
				try {
					return departmentDao.findById(Integer.valueOf(v.toString())).getName();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			return v;
		});
		return mapOb;
	}

	@Override
	public List<TbEmployees> getManagers(List<String> ids, TbEmployees entity) throws Exception {
		return employeesDao.findBydepId(ids, entity);
	}

	@Override
	public List<TbEmployees> getEmployees(TbEmployees entity) throws Exception {
		return employeesDao.getList(entity);
	}

	@Override
	public void addUser(String name, String chineseName, String dep, String onBoardDate, String groupId)
			throws Exception {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		String time = df.format(d);
		Date createdAt = df.parse(time);

		TbEmployees user = new TbEmployees();
		user.setUsername(name);
		if (dep.equals("1") || dep.equals("15")) {
			user.setGroupId(dep.equals("1") ? dep : "2");
		} else {
			user.setGroupId(groupId);
		}

		if (!onBoardDate.isEmpty()) {
			user.setOnBoardDate(DateUtil.StringToDate(onBoardDate));
		}
		user.setStatus("Y");
		user.setCreatedAt(createdAt);
		user.setUpdatedAt(createdAt);
		user.setDepartmentId(dep);
		user.setChineseName(chineseName);

		employeesDao.save(user);
	}

	@Override
	public void editUser(String name, String dep, String groupId, String status) throws Exception {
		TbEmployees model = employeesDao.findByUserName(name).get(0);// 舊資料
		List<TbDepartment> depList = departmentDao.getList();

		TbDepartment resultDep = depList.stream().filter(d -> d.getManager() != null).filter(d -> {
			if (name.equals(d.getManager().getUsername()) && !dep.equals(d.getId())) {
				return true;
			}
			return false;
		}).findAny().orElse(null);
		if (resultDep != null) {
			resultDep.setManager(null);
			departmentDao.save(resultDep);
		}
		if (!"1".equals(model.getGroupId())) {
			if (dep.equals("1") || dep.equals("15")) {
				model.setGroupId(dep.equals("1") ? dep : "2");
			} else {
				model.setGroupId("4");
			}
		}
		model.setUsername(name);
		model.setDepartmentId(dep);
		model.setStatus(status);
		model.setUpdatedAt(new Date());
		employeesDao.save(model);
	}

	@Override
	public void deleteUser(String name) throws Exception {
		TbEmployees emp = employeesDao.findByUserName(name).get(0);
		employeesDao.delete(emp);
	}

	@Override
	public List<TbEmployees> getEmployeesByCompany(String companyId, String live) throws Exception {
		List<TbDepartment> depsOfCompany = departmentDao.findByCompany(companyId);
		List<String> depIdsOfCompany = depsOfCompany.stream().map(d -> d.getId() + "").collect(Collectors.toList());
		TbEmployees tbemployees = null;
		if ("Y".equals(live)) {
			tbemployees = new TbEmployees();
			tbemployees.setStatus("Y");
		}
		List<TbEmployees> emps = employeesDao.findBydepId(depIdsOfCompany, tbemployees);
		return emps;
	}

}