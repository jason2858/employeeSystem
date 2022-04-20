package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbRejectCode;

public interface RejectedCodeDao {

	/**
	 * @param code
	 * @return
	 * 取得TbRejectCode內hashcode符合code的資料
	 */
	public TbRejectCode  findByCode(String code);
	
	/**
	 * @param Object
	 * @throws Exception
	 * 將此筆TbRejectCode Object存入資料庫
	 */
	public void save(TbRejectCode Object) throws Exception;
	

	public void deleteBySchdulesId(Integer id) throws Exception;
}