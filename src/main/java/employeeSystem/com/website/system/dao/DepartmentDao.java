package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;

public interface DepartmentDao {
	/**
	 * @param department
	 * @throws Exception
	 *  將此筆TbDepartment department存入資料庫
	 */
	public void save(TbDepartment department) throws Exception;

	/**
	 * @param authorise
	 * @param manager
	 * @throws Exception
	 *修改TbEmployees內username符合manager的group_id為authorise
	 */
	public void updateAuthorise(String authorise, String manager) throws Exception;

	/**
	 * @param department
	 * @throws Exception
	 * 將符合此筆TbDepartment department的資料從資料庫刪除
	 */
	public void delete(TbDepartment department) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 *  取出TbDepartment內所有資料
	 */
	public List<TbDepartment> getList() throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出符合id的TbDepartment資料
	 */
	public TbDepartment findById(Integer id) throws Exception;

	/**
	 * @param ids
	 * @return
	 * @throws Exception
	 * 取出TbDepartment內符合ids的資料
	 */
	public List<TbDepartment> findByDepartmentIds(List<Integer> ids) throws Exception;

	/**
	 * @param account
	 * @return
	 * @throws Exception
	 * 取出TbDepartment內manager符合account的資料
	 */
	public List<TbDepartment> findByManager(String account) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbDepartment內companyId符合id的資料
	 */
	public List<TbDepartment> findByCompany(String id) throws Exception;
}