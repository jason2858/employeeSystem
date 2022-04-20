package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbScheduledLeave;
import com.yesee.gov.website.model.TbScheduledLeaveId;

public interface ScheduledLeaveDao {

	/**
	 * @param object
	 * @throws Exception
	 * 將此筆TbScheduledLeave object存入資料庫
	 */
	public void save(TbScheduledLeave object) throws Exception;

	/**
	 * @param object
	 * @throws Exception
	 * 將符合此筆TbScheduledLeave object的資料從資料庫中刪除
	 */
	public void delete(TbScheduledLeave object) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 * 取出TbScheduledLeave內所有資料
	 */
	public List<TbScheduledLeave> getList() throws Exception;
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbScheduledLeave內empName符合EmpName且skdDate符合SkdDate的資料
	 */
	public List<TbScheduledLeave> findById(TbScheduledLeaveId id) throws Exception;
	
	/**
	 * @param empName
	 * @return
	 * @throws Exception
	 * 取出TbScheduledLeave內符合empName的資料
	 */
	public List<TbScheduledLeave> findByEmployeesName(String empName) throws Exception;
	
	/**
	 * @param year
	 * @param emp
	 * @return
	 * @throws Exception
	 * 取出TbScheduledLeave內skdDate內容包含year且empName符合emp的資料
	 */
	public List<TbScheduledLeave> findByYearAndEmployees(String year, String emp) throws Exception;
	
}