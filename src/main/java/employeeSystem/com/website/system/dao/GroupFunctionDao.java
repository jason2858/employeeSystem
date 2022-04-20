package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbGroupFunction;

public interface GroupFunctionDao {
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbGroupFunction內groupId符合id的資料
	 */
	public List<TbGroupFunction> getListById(String id) throws Exception;

}