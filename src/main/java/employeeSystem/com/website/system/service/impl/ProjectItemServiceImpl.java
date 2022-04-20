package com.yesee.gov.website.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.dao.ProjectDao;
import com.yesee.gov.website.dao.ProjectItemDao;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItem;
import com.yesee.gov.website.service.ProjectItemService;
import com.yesee.gov.website.service.ProjectService;
import com.yesee.gov.website.service.WorkItemService;

@Service("projectItemService")
public class ProjectItemServiceImpl implements ProjectItemService {

	@Autowired
	private WorkItemService workItemService;

	@Autowired
	private ProjectItemDao projectItemDao;

	@Override
	public TbProjectItem getProjectItemById(Integer id) throws Exception {
		return projectItemDao.findById(id);
	}

	@Override
	public List<TbProjectItem> getList(Integer authorise, String account) throws Exception {
		List<String> status = new ArrayList<>();
		status.add("CREATED");
		status.add("SIGNED");
		status.add("DELETED");
		if (authorise == 1) {
			return projectItemDao.getList(status);
		} else {
			List<TbProject> list = workItemService.getPersonalProject(account);
			List<Integer> project_ids = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				project_ids.add(Integer.valueOf(list.get(i).getId()));
			}
			return projectItemDao.getListByProejctId(project_ids, status);
		}

	}

	@Override
	public List<TbProjectItem> getAllValidList() throws Exception {
		List<String> status = new ArrayList<>();
		status.add("SIGNED");
		status.add("DELETED");
		return projectItemDao.getList(status);
	}
	
	@Override
	public void save(TbProjectItem object) throws Exception {
		projectItemDao.save(object);
	}

	@Override
	public void delete(TbProjectItem object) throws Exception {
		projectItemDao.delete(object);
	}
}