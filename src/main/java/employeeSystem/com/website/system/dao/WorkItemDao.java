package com.yesee.gov.website.dao;

import java.util.Date;
import java.util.List;

import com.yesee.gov.website.model.TbWorkItem;

public interface WorkItemDao {
	/**
	 * @param account
	 * @param date
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內emp_name符合account且date符合且STATUS不等於CANCELLED的資料
	 */
	public List<TbWorkItem> findHour(String account, String date) throws Exception;

	/**
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內emp_name符合account且date介於startDate到endDate且STATUS不等於CANCELLED的資料，以created_at欄位降序排列
	 */
	public List<TbWorkItem> findRecords(String account, Date startDate, Date endDate) throws Exception;
	
	/**
	 * @param accounts
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內emp_name符合accounts且date介於startDate到endDate且STATUS不等於CANCELLED的資料，以created_at欄位降序排列
	 */
	public List<TbWorkItem> findRecordsByList(List<String> accounts, Date startDate, Date endDate) throws Exception;
	
	/**
	 * @param id
	 * @param accounts
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內pro_id符合id且emp_name符合accounts且date介於startDate到endDate且STATUS不等於CANCELLED的資料，以created_at欄位降序排列
	 */
	public List<TbWorkItem> findRecordsByListAndProjectId(Integer id, List<String> accounts, Date startDate,
			Date endDate) throws Exception;
	
	/**
	 * @param account
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內emp_name符合account且STATUS不等於CANCELLED並以proId做GROUP BY，以date最大值降序排列
	 */
	public List<Integer> findRecentItem(String account) throws Exception;
	
	public List<TbWorkItem> findRecordsByItems(List<Integer> ids) throws Exception;
	public List<TbWorkItem> findRecordsByProjects(List<Integer> ids) throws Exception;

	/**
	 * @param Object
	 * @throws Exception
	 * 將此筆TbWorkItem Object存入資料庫
	 */
	public void save(TbWorkItem Object) throws Exception;

	/**
	 * @param Object
	 * @throws Exception
	 * 將符合此筆TbWorkItem Object的資料從資料庫中刪除
	 */
	public void delete(TbWorkItem Object) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbWorkItem內符合id的資料
	 */
	public TbWorkItem findById(Integer id) throws Exception;

}