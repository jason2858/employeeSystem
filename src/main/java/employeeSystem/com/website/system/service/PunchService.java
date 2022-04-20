package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbPunchRecords;

public interface PunchService {
	/**
	 * @param user
	 * @return List<TbPunchRecords>
	 * 依照帳號，取得打卡表單資訊
	 * @throws Exception
	 */
	public List<TbPunchRecords> getPunchStatus(String user) throws Exception;

	/**
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return List<TbPunchRecords>
	 * 依照起迄日期以及帳號，取得補打卡表單資訊
	 * @throws Exception
	 */
	public List<TbPunchRecords> getMakeUpCount(String user, String startDate, String endDate) throws Exception;

	/**
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return List<TbPunchRecords>
	 * 依照起迄日期已及帳號，取得打卡資訊
	 * @throws Exception
	 */
	public List<TbPunchRecords> getRecords(String user, String startDate, String endDate) throws Exception;

	/**
	 * @param startDate
	 * @param endDate
	 * @return Map<String, Object>
	 * 依照起訖日期，取得假期資訊
	 * @throws Exception
	 */
	public Map<String, Object> getHoliday(String startDate, String endDate) throws Exception;

	/**
	 * @param user
	 * @param startDate
	 * @param endDate
	 * @return Map<String, Object>
	 * 依照起訖日期以及帳號，取得差勤資訊
	 * @throws Exception
	 */
	public Map<String, Object> getAttendance(String user, String startDate, String endDate) throws Exception;

	/**
	 * @param record
	 * @return int
	 * 普通打卡
	 * @throws Exception
	 */
	public int punch(TbPunchRecords record) throws Exception;

	/**
	 * @param record
	 * @return int
	 * 補打卡
	 * @throws Exception
	 */
	public int makeUp(TbPunchRecords record) throws Exception;

	/**
	 * @param id
	 * @return int
	 * 刪除打卡資訊
	 * @throws Exception
	 */
	public int del(Long id) throws Exception;

	/**
	 * @param nameSelect
	 * @param depId
	 * @param authorise
	 * @param account
	 * @return List<Map<String, Object>>
	 * 取得未簽合補打卡紀錄
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUnsignedMakeUpRecords(String nameSelect, String depId, String authorise,
			String account) throws Exception;

	/**
	 * @param account
	 * @param id
	 * @param status
	 * @param reason
	 * @return int
	 * 簽核前確認資料是否有被修改並簽核或駁回
	 * @throws Exception
	 */
	public int checkMakeUpAndUpdate(String account, String id, String status, String reason) throws Exception;

	/**
	 * @param account
	 * @param record
	 * @param status
	 * @param reason
	 * 更新補打卡狀態
	 * @throws Exception
	 */
	public void updateMakeUpStatus(String account, TbPunchRecords record, String status, String reason)
			throws Exception;

	/**
	 * @param nameSelect
	 * @param status
	 * @param depId
	 * @param authorise
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String, Object>>
	 * 取得補打卡紀錄
	 * @throws Exception
	 */
	public List<Map<String, Object>> getMakeUpRecords(String nameSelect, String status, String depId, String authorise,
			String account, String startDate, String endDate) throws Exception;
}
