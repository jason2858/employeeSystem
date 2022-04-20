package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbPunchRecords;

public interface PunchRecordsDao {
	/**
	 * @param user
	 * @param type
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內user符合user且type符合type或makeup+type值且DATE(punch_time)為今日且status符合CREATED或SIGNED，以punch_time欄位降序排列
	 */
	public List<TbPunchRecords> findPunchStatus(String user, String type) throws Exception;

	/**
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內符合user且punch_time介於startDate到endDate且type為makeupin或makeupout且status為CREATED或SIGNED的資料
	 */
	public List<TbPunchRecords> findMakeUpCount(String user, String startDate, String endDate) throws Exception;

	/**
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內符合user且punch_time介於startDate到endDate且status不等於CANCELLED或REJECTED，以punch_time欄位降序排列
	 */
	public List<TbPunchRecords> getRecords(String user, String startDate, String endDate) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內符合id的資料
	 */
	public TbPunchRecords findById(Long id) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 * 使用TbPunchRecords record的Username、Type、PunchTime與TbPunchRecords比對且type取出符合makeup+type值與type值的資料且status為CREATED或SIGNED的資料
	 */
	public List<TbPunchRecords> findPunch(TbPunchRecords record) throws Exception;

	/**
	 * @param record
	 * @return
	 * @throws Exception
	 * 使用TbPunchRecords record的Username、Type、PunchTime與TbPunchRecords比對且type取出符合type值且status為CREATED的資料
	 */
	public List<TbPunchRecords> findMakeUp(TbPunchRecords record) throws Exception;
	
	/**
	 * @param users
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內user符合users且type符合makeupin或makeupout且status為CREATED的資料
	 */
	public Integer getUnsignCount(List<String> users) throws Exception;

	/**
	 * @param record
	 * @throws Exception
	 * 將此筆TbPunchRecords record存入資料庫
	 */
	public void save(TbPunchRecords record) throws Exception;

	/**
	 * @param id
	 * @throws Exception
	 * 將TbPunchRecords內符合id資料的status設為CANCELLED
	 */
	public void del(Long id) throws Exception;

	/**
	 * @param nameList
	 * @param status
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內user符合nameList且status符合且punch_time介於startDate到endDate且type為makeupin或makeupout的資料
	 */
	public List<TbPunchRecords> findMakeUpByStatus(List<String> nameList, String status, String startDate,
			String endDate) throws Exception;

	/**
	 * @param nameList
	 * @param status
	 * @return
	 * @throws Exception
	 * 取出TbPunchRecords內user符合nameList且status符合且type為makeupin或makeupout的資料
	 */
	public List<TbPunchRecords> findAllMakeUpByStatus(List<String> nameList, String status) throws Exception;
}
