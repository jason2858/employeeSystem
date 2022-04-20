package com.yesee.gov.website.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.dao.ProjectMemberDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProjectMember;
import com.yesee.gov.website.service.ProjectMemberService;

@Service("projectMemberService")
public class ProjectMemberServiceImpl implements ProjectMemberService {

	@Autowired
	private ProjectMemberDao projectMemberDao;

	@Autowired
	private EmployeesDao employeesDao;

	@Override
	public List<TbEmployees> getNotMemberEmployees(Integer projectId) throws Exception {
		TbEmployees entity = new TbEmployees();
		entity.setStatus("Y");
		Set<String> username = new HashSet<String>();
		List<TbEmployees> employees = employeesDao.getList(entity);
		List<Integer> project = new ArrayList<Integer>();
		project.add(projectId);
		List<TbProjectMember> member = projectMemberDao.getByProjectIds(project);
		List<TbEmployees> result = new ArrayList<TbEmployees>();
		for (int i = 0; i < member.size(); i++) {
			username.add(member.get(i).getTbEmployees().getUsername());
		}
		for(int i = 0;i<employees.size();i++) {
			if(!username.contains(employees.get(i).getUsername())) {
				result.add(employees.get(i));
			}
		}
		return result;
	}

	@Override
	public List<TbProjectMember> getList(List<Integer> projects) throws Exception{
		return projectMemberDao.getByProjectIds(projects);
	}
	
	@Override
	public void save(TbProjectMember object) throws Exception {
		projectMemberDao.save(object);
	}
	
	@Override
	public void del(Integer id) throws Exception {
		projectMemberDao.delete(projectMemberDao.findById(id));
	}
}