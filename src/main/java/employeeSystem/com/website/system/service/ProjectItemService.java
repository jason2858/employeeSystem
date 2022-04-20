package com.yesee.gov.website.service;

import java.util.List;

import com.yesee.gov.website.model.TbProjectItem;

public interface ProjectItemService {
	
	public TbProjectItem getProjectItemById(Integer id) throws Exception;
	
	public List<TbProjectItem> getList(Integer authorise, String account) throws Exception;
	
	public List<TbProjectItem> getAllValidList() throws Exception;

	public void save(TbProjectItem object) throws Exception;

	public void delete(TbProjectItem object) throws Exception;
	
}
