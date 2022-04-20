package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbEmployees;

public interface EmployeesService {
	/**
	 * @param account
	 * @param companyId
	 * @return Map<String, Object>
	 * 依照公司，取得員工資料
	 * @throws Exception
	 */
	public Map<String, Object> getEmployeesInfo(String account, String companyId) throws Exception;

	/**
	 * @param entity
	 * @return List<TbEmployees>
	 * 依照輸入員工物件，取得員工列表
	 * @throws Exception
	 */
	public List<TbEmployees> getEmployees(TbEmployees entity) throws Exception;

	/**
	 * @param companyId
	 * @param live
	 * @return List<TbEmployees>
	 * 依照公司，取得在職員工資料
	 * @throws Exception
	 */
	public List<TbEmployees> getEmployeesByCompany(String companyId, String live) throws Exception;

	/**
	 * @param ids
	 * @param entity
	 * @return List<TbEmployees>
	 * 依照部門集合，取得員工資料表
	 * @throws Exception
	 */
	public List<TbEmployees> getManagers(List<String> ids, TbEmployees entity) throws Exception;

	/**
	 * @param name
	 * @param chineseName
	 * @param dep
	 * @param onBoardDate
	 * @param groupId
	 * 新增員工
	 * @throws Exception
	 */
	public void addUser(String name, String chineseName, String dep, String onBoardDate, String groupId)
			throws Exception;

	/**
	 * @param name
	 * @param parent
	 * @param groupId
	 * @param status
	 * 更新員工
	 * @throws Exception
	 */
	public void editUser(String name, String parent, String groupId, String status) throws Exception;

	/**
	 * @param name
	 * 刪除員工
	 * @throws Exception
	 */
	public void deleteUser(String name) throws Exception;
}
