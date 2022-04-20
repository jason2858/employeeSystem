package com.yesee.gov.website.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectItemSort;
import com.yesee.gov.website.model.TbWorkItem;

public interface WorkItemService {
	/**
	 * @param account
	 * @param date
	 * @return float
	 * 取得當天剩餘工時
	 * @throws Exception
	 */
	public float getMainHour(String account, String date) throws Exception;

	/**
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return List<TbWorkItem>
	 * 依照起訖日期及帳號，取得工時紀錄表單
	 * @throws Exception
	 */
	public List<TbWorkItem> getRecords(String account, Date startDate, Date endDate) throws Exception;

	/**
	 * @param accounts
	 * @param startDate
	 * @param endDate
	 * @return  List<TbWorkItem>
	 * 依照起訖日期以及帳號表單，取得工時紀錄表單
	 * @throws Exception
	 */
	public List<TbWorkItem> getRecordsByList(List<String> accounts, Date startDate, Date endDate) throws Exception;

	/**
	 * @param id
	 * @param accounts
	 * @param startDate
	 * @param endDate
	 * @return List<TbWorkItem>
	 * 依照起訖日期以及專案，取得工時紀錄表單
	 * @throws Exception
	 */
	public List<TbWorkItem> getRecordsByListAndProjectId(Integer id, List<String> accounts, Date startDate,
			Date endDate) throws Exception;

	/**
	 * @param account
	 * @return ArrayList<String>
	 * 抓最近選的三種專案
	 * @throws Exception
	 */
	public ArrayList<String> getRecentItem(String account) throws Exception;
	


	/**
	 * @param condition
	 * @return Map<String, Object>
	 * 依照專案狀態，取得專案物件
	 * @throws Exception
	 */
	public Map<String, Object> getProjectName(String condition) throws Exception;
	
	public List<TbProject> getPersonalProject(String user) throws Exception;
	
	public List<TbWorkItem> getRecordsByItem(Integer id) throws Exception;
	
	public Map<Integer, Float> getActualHour(List<Integer> ids) throws Exception;

	/**
	 * @param record
	 * 儲存工時紀錄
	 * @throws Exception
	 */
	public void save(TbWorkItem record) throws Exception;

	/**
	 * @param id
	 * 刪除工時紀錄
	 * @throws Exception
	 */
	public void del(Integer id) throws Exception;
	

	public List<TbProjectItemSort> getItemSort() throws Exception;
}
