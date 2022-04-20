package com.yesee.gov.website.service;

import java.util.List;

import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbProject;
import com.yesee.gov.website.model.TbProjectType;
import com.yesee.gov.website.pojo.ProjectVO;

public interface ProjectService {

	/**
	 * @param nameSelect
	 * @param owner
	 * @param authorise
	 * @return List<ProjectVO>
	 * 依照帳號，取得其專案表單
	 * @throws Exception
	 */
	public List<ProjectVO> getList(String nameSelect, String owner, String authorise) throws Exception;

	/**
	 * @return List<TbProjectType>
	 * 取得專案類別表單
	 * @throws Exception
	 */
	public List<TbProjectType> getTypeList() throws Exception;

	/**
	 * @return List<TbCustomer>
	 * 取得簽核過未被刪除客戶表單
	 * @throws Exception
	 */
	public List<TbCustomer> getCustomerList() throws Exception;
	
	/**
	 * @return List<TbDepartment>
	 * 取得部門表單
	 * @throws Exception
	 */
	public List<TbDepartment> getDepList() throws Exception;
	
	/**
	 * @param project
	 * 儲存專案
	 * @throws Exception
	 */
	public void save(ProjectVO project) throws Exception;
	
	/**
	 * @param project
	 * 刪除專案
	 * @throws Exception
	 */
	public void delete(TbProject object) throws Exception;
	
	/**
	 * @param project
	 * 更新專案
	 * @throws Exception
	 */
	public void update(ProjectVO project, TbProject object) throws Exception;
	
	/**
	 * @param id
	 * 簽核申請之專案
	 * @throws Exception
	 */
	public void sign(TbProject object) throws Exception;
	
	/**
	 * @param id
	 * @return TbProject
	 * 依照ID，取得專案
	 * @throws Exception
	 */
	public TbProject findById(Integer id) throws Exception;
	
	/**
	 * @param vo
	 * @return
	 * 檢查專案是否已被更新
	 * @throws Exception
	 */
	public TbProject checkUpdate(ProjectVO vo) throws Exception;
	/**
	 * @param entity
	 * @return List<TbEmployees>
	 * 依照輸入員工物件取得員工表單
	 * @throws Exception
	 */
	public List<TbEmployees> getEmpList(TbEmployees entity) throws Exception;
	
	public List<TbProject> getAllProject() throws Exception;
	
}
