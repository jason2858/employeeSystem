package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbAnnualLeave;
import com.yesee.gov.website.model.TbAnnualLeaveId;

public interface AnnualLeaveDao {

	/**
	 * @param object
	 * @throws Exception
	 * 將此筆TbAnnualLeave object存入資料庫
	 */
	public void save(TbAnnualLeave object) throws Exception;

	/**
	 * @param object
	 * @throws Exception
	 *將符合此筆TbAnnualLeave object的資料從資料庫刪除
	 */
	public void delete(TbAnnualLeave object) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 * 取出TbAnnualLeave內所有資料
	 */
	public List<TbAnnualLeave> getList() throws Exception;

	/**
	 * @param empOfCompany
	 * @return
	 * @throws Exception
	 * 以empOfCompany內資料比對empName欄位取出TbAnnualLeave資料
	 */
	public List<TbAnnualLeave> getListByEmployees(List<String> empOfCompany) throws Exception;

	/**
	 * @param tbAnnuaLeaveId
	 * @return
	 * @throws Exception
	 * 以TbAnnualLeaveId tbAnnuaLeaveId內的EmpName與Year從TbAnnualLeave取出同時符合的資料
	 */
	public List<TbAnnualLeave> findById(TbAnnualLeaveId tbAnnuaLeaveId) throws Exception;

	/**
	 * @param year
	 * @return
	 * @throws Exception
	 * 取出符合year的TbAnnualLeave資料
	 */
	public List<TbAnnualLeave> getListByYear(int year) throws Exception;

	/**
	 * @param account
	 * @param year
	 * @return
	 * @throws Exception
	 * 取出同時符合account與year的TbAnnualLeave資料
	 */
	public List<TbAnnualLeave> findByNameAndYear(String account, String year) throws Exception;

}