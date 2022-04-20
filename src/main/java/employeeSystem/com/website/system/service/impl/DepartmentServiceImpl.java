package com.yesee.gov.website.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.service.DepartmentService;

@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {
	private static final Logger logger = LogManager.getLogger(DepartmentServiceImpl.class);
	
	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private EmployeesDao employeesDao;

	@Override
	public List<TbDepartment> getRecords() throws Exception {
		return departmentDao.getList();
	}

	@Override
	public Map<String, Integer> getSum() throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<TbEmployees> list = employeesDao.getList(null);
		Iterator<TbEmployees> it = list.iterator();
		String id;
		while (it.hasNext()) {
			TbEmployees emp = it.next();
			if (emp.getDepartmentId() != null) {
				id = emp.getDepartmentId();
				if(map.get(id)== null) {
					map.put(id, 0);
				}
				map.put(id, map.get(id) + 1);
			}
		}
		return map;
	}

	@Override
	public void updateDepartment(TbDepartment record, String manager) throws Exception {
		departmentDao.save(record);
		if (!manager.equals("add") && record.getId() != 1 && record.getId() != 15) {
			if (record.getManager() != null && !record.getManager().getUsername().equals("admin")) {
				departmentDao.updateAuthorise("3", record.getManager().getUsername());
			}
			if (!manager.equals("無") && !manager.equals("admin")) {
				departmentDao.updateAuthorise("4", manager);
			}
		}
		//不同公司更新部門時，子部門的companyId轉換
		if (record.getParentId() != null) {
			if (!"add".equals(manager)) {
				String rootDepId = record.getId() + "";
				Set<String> subDeps = this.getChildDepartments(rootDepId);
				if (subDeps.size() > 1) {
					subDeps.forEach(d -> {
						try {
							if (!d.equals(rootDepId)) {
								TbDepartment dep = departmentDao.findById(Integer.parseInt(d));
								dep.setCompanyId(record.getCompanyId());
								departmentDao.save(dep);
							}
						} catch (Exception e) {
							logger.error(e);
						}
					});
				}
			}
		}
	}

	@Override
	public void delDepartment(TbDepartment record) throws Exception {
		departmentDao.delete(record);
	}

	@Override
	public Set<String> getChildDepartments(String depId) throws Exception {
		Set<String> set = new HashSet<>();
		List<TbDepartment> list = departmentDao.getList();
		set.add(depId);
		int size;
		do {
			size = set.size();
			for (int i = 0; i < list.size(); i++) {
				if (set.contains(String.valueOf(list.get(i).getParentId()))) {
					set.add(String.valueOf(list.get(i).getId()));
					list.remove(i);
				}
			}
		} while (set.size() != size);
		return set;
	}

	@Override
	public Set<String> getParentDepartments(String depId) throws Exception {
		Set<String> set = new HashSet<>();
		List<TbDepartment> list = departmentDao.getList();
		int size, id = Integer.parseInt(depId);
		set.add(depId);
		do {
			size = set.size();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getId() == id) {
					if (list.get(i).getParentId() != null) {
						id = list.get(i).getParentId();
						set.add(String.valueOf(list.get(i).getParentId()));
						list.remove(i);
					}
				}
			}
		} while (set.size() != size);
		return set;
	}

	@Override
	public TbDepartment findDepartmentById(String id) throws Exception {
		return departmentDao.findById(Integer.parseInt(id));
	}

	@Override
	public List<TbDepartment> getListByCompanyId(String companyId) throws Exception {
		return departmentDao.findByCompany(companyId);
	}

	@Override
	public List<TbDepartment> getDepListByIds(List<Integer> ids) throws Exception {
		return departmentDao.findByDepartmentIds(ids);
	}
}