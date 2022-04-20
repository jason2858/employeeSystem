package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbProjectType;

public interface ProjectTypeDao {
	/**
	 * @param projectType
	 * @throws Exception
	 * 將此筆TbProjectType projectType存入資料庫
	 */
	public void save(TbProjectType projectType) throws Exception;

	/**
	 * @param projectType
	 * @throws Exception
	 * 將符合此筆TbProjectType projectType的資料從資料庫中刪除
	 */
	public void delete(TbProjectType projectType) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 * 取出TbProjectType內所有資料
	 */
	public List<TbProjectType> getList() throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbProjectType內id符合的TbProjectType資料
	 */
	public TbProjectType findById(Integer id) throws Exception;

}