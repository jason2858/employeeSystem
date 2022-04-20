package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbPreference;

public interface PreferenceDao {
	
	/**
	 * @param object
	 * @throws Exception
	 * 將此筆TbPreference object存入資料庫
	 */
	public void save(TbPreference object) throws Exception;

	/**
	 * @param object
	 * @throws Exception
	 * 將符合TbPreference object的資料從資料庫中刪除
	 */
	public void delete(TbPreference object) throws Exception;

	/**
	 * @param user
	 * @param key
	 * @return
	 * @throws Exception
	 * 取出TbPreference中username符合user且config_key符合key的資料，若無則回傳null
	 */
	public TbPreference getByUserAndKey(String user,String key) throws Exception;
	
	/**
	 * @param user
	 * @return
	 * @throws Exception
	 * 取出TbPreference中username符合user的資料，若無則回傳null
	 */
	public List<TbPreference> getByUser(String user) throws Exception;

}