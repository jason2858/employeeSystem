package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbProjectItem;

public interface ProjectItemDao {
	
	public TbProjectItem findById(Integer id) throws Exception;
	
	public List<TbProjectItem> getList(List<String> status) throws Exception;
	
	public List<TbProjectItem> getListByProejctId(List<Integer> ids, List<String> status) throws Exception;

	public void save(TbProjectItem object) throws Exception;
	
	public void delete(TbProjectItem object) throws Exception;
	
	public Integer getUnsignCount()throws Exception;
	
	public Integer getUnsignCount(List<Integer> projectIds)throws Exception;
}