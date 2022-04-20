package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbProjectMember;

public interface ProjectMemberDao {
	
	/**
	 * @param object
	 * @throws Exception
	 *  將此筆TbProjectMember object存入資料庫
	 */
	public void save(TbProjectMember object) throws Exception;

	/**
	 * @param object
	 * @throws Exception
	 * 將符合此筆TbProjectMember object的資料從資料庫刪除
	 */
	public void delete(TbProjectMember object) throws Exception;
	
	public TbProjectMember findById(Integer id) throws Exception;
	
	public List<TbProjectMember> getByProjectIds(List<Integer> ids) throws Exception;

	public List<TbProjectMember> getByName(String user) throws Exception;
}