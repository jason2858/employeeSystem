package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.pojo.AnnualLeaveVo;
import com.yesee.gov.website.pojo.ScheduledLeaveVo;

public interface AccountService {
	/**
	 * @param account
	 * @return TbEmployees 依照帳號取得員工物件
	 * @throws Exception
	 */
	public TbEmployees getByUserName(String account) throws Exception;

	/**
	 * @param account
	 * @return boolean 依照帳號判斷是否為人事主管
	 * @throws Exception
	 */
	public boolean isHRM(String account) throws Exception;

	/**
	 * @param account
	 * @return Set<String> 依照帳號取得人事帳號集合
	 * @throws Exception
	 */
	public Set<String> getHR(String account) throws Exception;

	/**
	 * @param Authorise
	 * @return Map<String, Object> 依照權限取得，側邊物件的集合
	 * @throws Exception
	 */
	public Map<String, Object> getSidebar(int Authorise) throws Exception;

	/**
	 * @param depId
	 * @param authorise
	 * @param account
	 * @param HRM
	 * @return int 取得未簽核數量
	 * @throws Exception
	 */
	public int getUnsign(String depId, int authorise, String account, String HRM) throws Exception;

	/**
	 * @param authorise
	 * @return Map<String, Object> 依照權限取得專案以及客戶未簽核物件
	 * @throws Exception
	 */
	public Map<String, Object> getProjectAndCustomerUnsign(int authorise, String account) throws Exception;

	/**
	 * @param annualLeaveVo 儲存員工年度特休時數
	 * @throws Exception
	 */
	public void saveAnnualLeave(AnnualLeaveVo annualLeaveVo) throws Exception;

	/**
	 * @param annualLeaveVo 更新員工年度特休時數
	 * @throws Exception
	 */
	public void updAnnualLeave(AnnualLeaveVo annualLeaveVo) throws Exception;

	/**
	 * @param annualLeaveVo 刪除員工年度特休時數
	 * @throws Exception
	 */
	public void deleteAnnualLeave(AnnualLeaveVo annualLeaveVo) throws Exception;

	/**
	 * @param nameSelect
	 * @param companyId
	 * @param authorise
	 * @return List<AnnualLeaveVo> 取得員工年度特休時數
	 * @throws Exception
	 */
	public List<AnnualLeaveVo> getAnnualList(String nameSelect, String companyId, int authorise) throws Exception;

	/**
	 * @param annualLeaveVo
	 * @return AnnualLeaveVo 依照所選項目，取得年度特休資料
	 * @throws Exception
	 */
	public AnnualLeaveVo findAnnualLeaveById(AnnualLeaveVo annualLeaveVo) throws Exception;

	/**
	 * @param skdLeavevo 儲存員工預排特休
	 * @throws Exception
	 */
	public void saveScheduledLeave(ScheduledLeaveVo skdLeavevo) throws Exception;

	/**
	 * @param emp
	 * @param year
	 * @return ScheduledLeaveVo 依照員工及年度，取得預排特休物件
	 * @throws Exception
	 */
	public ScheduledLeaveVo findSchedulesLeaveByEmployees(String emp, String year) throws Exception;

	/**
	 * @param nameSelect
	 * @param empName
	 * @return List<AnnualLeaveVo> 取得現時特休檢閱資料
	 * @throws Exception
	 */
	public List<AnnualLeaveVo> getAnnualLeaveOfMonth(String nameSelect, String empName) throws Exception;

	/**
	 * @param empName
	 * @return List<TbEmployees> 依帳號取得員工資料
	 * @throws Exception
	 */
	public Optional<List<TbEmployees>> getEmployeesByName(String empName) throws Exception;

	/**
	 * @param empName
	 * @return Map<Integer, String> 依帳號取得取得部門表
	 * @throws Exception
	 */
	public Map<Integer, String> getDepList(String empName) throws Exception;

	/**
	 * @param authorise
	 * @param depId
	 * @param entity
	 * @return List<TbEmployees> 取得部門以下所有員工資料
	 * @throws Exception
	 */
	public List<TbEmployees> getSubordinateList(String authorise, String depId, TbEmployees entity) throws Exception;

	/**
	 * @param depId
	 * @param account
	 * @return List<TbEmployees> 取得申請後主管名單資料
	 * @throws Exception
	 */
	public List<TbEmployees> getApplyMailRecipient(String depId, String account) throws Exception;

	/**
	 * @param depId
	 * @param account
	 * @return List<TbEmployees> 取得駁回或簽核後寄信的員工名單
	 * @throws Exception
	 */
	public List<TbEmployees> getRespondMailRecipient(String depId, String account) throws Exception;

	/**
	 * @param department
	 * @return List<TbEmployees> 依照部門取得員工資料
	 * @throws Exception
	 */
	public List<TbEmployees> getByDepartmentSet(Set<String> department) throws Exception;
}
