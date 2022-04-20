package com.yesee.gov.website.dao;

import com.yesee.gov.website.model.TbDeleteCode;

public interface DeleteCodeDao {

	/**
	 * @param code
	 * @return
	 * 取出TbDeleteCode內hashcode符合code的資料
	 */
	public TbDeleteCode findByCode(String code);
	
	/**
	 * @param Object
	 * @throws Exception
	 * 將此筆TbDeleteCode Object存入資料庫
	 */
	public void save(TbDeleteCode Object) throws Exception;
	
	public void deleteBySchdulesId(Integer id) throws Exception;
}