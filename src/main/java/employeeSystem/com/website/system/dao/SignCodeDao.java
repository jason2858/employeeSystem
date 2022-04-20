package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbSignCode;

public interface SignCodeDao {

	/**
	 * @param code
	 * @return
	 * 取出TbSignCode內hashcode符合code的資料
	 */
	public TbSignCode  findByCode(String code);
	
	/**
	 * @param Object
	 * @throws Exception
	 * 將此筆TbSignCode Object存入資料庫
	 */
	public void save(TbSignCode Object) throws Exception;
	
	public void deleteBySchdulesId(Integer id) throws Exception;
}