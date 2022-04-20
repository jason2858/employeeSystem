package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbCompany;

public interface CompanyDao {
	
	/**
	 * @param object
	 * @throws Exception
	 *  將此筆TbCompany object存入資料庫
	 */
	public void save(TbCompany object) throws Exception;

	/**
	 * @param object
	 * @throws Exception
	 * 將符合此筆TbAnnualLeave object的資料從資料庫刪除
	 */
	public void delete(TbCompany object) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 *  取出TbCompany內所有資料
	 */
	public List<TbCompany> getList() throws Exception;

}