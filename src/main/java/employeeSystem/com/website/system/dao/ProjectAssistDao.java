package com.yesee.gov.website.dao;

import com.yesee.gov.website.model.TbProjectAssist;

public interface ProjectAssistDao {
	/**
	 * @param projectAssist
	 * @throws Exception
	 * 將此筆TbProjectAssist projectAssist存入資料庫
	 */
	public void save(TbProjectAssist projectAssist) throws Exception;

	/**
	 * @param projectAssist
	 * @throws Exception
	 * 將符合此筆TbProjectAssist projectAssist的資料從資料庫中刪除
	 */
	public void delete(TbProjectAssist projectAssist) throws Exception;

}