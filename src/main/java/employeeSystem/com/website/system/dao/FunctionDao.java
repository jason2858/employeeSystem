package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbFunction;

public interface FunctionDao {
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbFunction符合id的資料若無則回傳null
	 */
	public List<TbFunction> getListById(List<String> ids) throws Exception;

}