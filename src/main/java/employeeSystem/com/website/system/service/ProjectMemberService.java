package com.yesee.gov.website.service;

import java.util.List;

import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProjectMember;

public interface ProjectMemberService {

	public List<TbEmployees> getNotMemberEmployees(Integer projectId) throws Exception;
	
	public List<TbProjectMember> getList(List<Integer> projects) throws Exception;

	public void save(TbProjectMember object) throws Exception;

	public void del(Integer id) throws Exception;

//	public TbProjectMember findById(Integer id) throws Exception;
	
}
