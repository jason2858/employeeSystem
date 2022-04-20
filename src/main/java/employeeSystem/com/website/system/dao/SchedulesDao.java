package com.yesee.gov.website.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.VTbSchedules;

public interface SchedulesDao {
	/**
	 * @param emp
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合emp的資料
	 */
	public List<TbSchedules> getSchedulesByEmployees(List<String> emp) throws Exception;

	/**
	 * @param emp
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合emp的資料
	 */
	public List<TbSchedules> getSchedulesByEmployees(String emp) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 * 取出TbSchedules的所有資料
	 */
	public List<TbSchedules> getList() throws Exception;
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內id符合的資料
	 */
	public TbSchedules getById(Integer id) throws Exception;
	
	/**
	 * @param users
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合users且status為CREATED的資料
	 */
	public Integer getUnsignCount (List<String> users) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內id符合的資料
	 */
	public TbSchedules findById(int id) throws Exception;

	/**
	 * @param nameList
	 * @param status
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合nameList的資料且status符合且start_time介於startDate到endDate的資料
	 */
	public List<TbSchedules> findScheduleByStatus(List<String> nameList, String status, String startDate,
			String endDate) throws Exception;

	/**
	 * @param nameList
	 * @param status
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合nameList且status符合的資料
	 */
//	public List<TbSchedules> findAllScheduleByStatus(List<String> nameList, List<String> status) throws Exception;
	public List<TbSchedules> findAllScheduleByStatus(List<String> nameList, String status) throws Exception;

	
	/**
	 * @param Object
	 * @return
	 * @throws Exception
	 * 以TbSchedules Object內資料比對TbSchedules並回傳id
	 */
	public void save(TbSchedules Object) throws Exception;
	
	/**
	 * @param Object
	 * @throws Exception
	 * 將符合此筆TbSchedules Object的資料從資料庫中刪除
	 */
	public void delete(TbSchedules Object) throws Exception;
	
	/**
	 * @param authorise
	 * @param account
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 * 取出TbSchedulesstatus為CREATED、SIGNED、REJECTED且user符合account且start_time與end_time都介於startTime到endTime的資料
	 */
//	public List<TbSchedules> getSchedulesInfo(int authorise, String account, String startTime, String endTime, List<String> statusFilter) throws Exception;
	public List<TbSchedules> getSchedulesInfo(int authorise, String account, String startTime, String endTime) throws Exception;

	/**
	 * @param empName
	 * @param type
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合empName且type符合的資料
	 */
	public List<TbSchedules> findByEmployeesAndType(String empName, Integer type) throws Exception;
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內id符合且status不為SIGNED或REJECTED的資料
	 */
	public int checkSignedOrRejected(Integer id) throws Exception;
	
	/**
	 * @param account
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內type為3且user符合account且status為SIGNED的資料
	 */
	public List<TbSchedules> getAnnualLeaveInfo(String account) throws Exception;

	/**
	 * @param name
	 * @param statusArray
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合name且status符合statusArray且start_time介於startDate到endDate的資料
	 */
	public List<TbSchedules> findAllScheduleByStatuses(String name, List<String> statusArray, String startDate, String endDate)
			throws Exception;
	
	/**
	 * @param name
	 * @param type
	 * @param statusArray
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合name且type符合且status符合statusArray且start_time介於startDate到endDate的資料
	 */
	public List<TbSchedules> findAllScheduleByType(String name, String type, List<String> statusArray, String startDate,
			String endDate) throws Exception;

	/**
	 * @param user
	 * @param status
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內user符合且status符合且start_time和endDate都介於end_time到endDate的資料
	 */
	public List<TbSchedules> findScheduleByDate(String user, String status, String startDate, String endDate) throws Exception;

	/**
	 * @param account
	 * @param year
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內startTime或endTime的year都符合且user符合account且status為SIGNED的資料
	 */
	public List<TbSchedules> findSchedulesOfYear(String account, Date year) throws Exception;
	
	/**
	 * @param createdAt
	 * @return
	 * @throws Exception
	 * 取出TbSchedules內DATE(created_at)符合createdAt且form_no不為null，以form_no欄位降序排列
	 */
	public List<TbSchedules> findTravelByCreatedAt(Date createdAt) throws Exception;
	
	/**
	 * @param account
	 * @return
	 * @throws Exception
	 * 取出VTbSchedules內user符合account的資料
	 */
	public List<VTbSchedules> findSchedulesOfAvailableTime(String account) throws Exception;
}
