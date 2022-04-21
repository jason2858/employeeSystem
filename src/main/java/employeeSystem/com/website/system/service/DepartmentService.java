package employeeSystem.com.website.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import employeeSystem.com.website.system.model.TbDepartment;

public interface DepartmentService {
	/**
	 * @return List<TbDepartment> 取得部門資料表單
	 * @throws Exception
	 */
	public List<TbDepartment> getRecords() throws Exception;

	/**
	 * @param companyId
	 * @return List<TbDepartment> 依照公司取得公司表單
	 * @throws Exception
	 */
	public List<TbDepartment> getListByCompanyId(String companyId) throws Exception;

	/**
	 * @return Map<String, Integer> 取得部門人數
	 * @throws Exception
	 */
	public Map<String, Integer> getSum() throws Exception;

	/**
	 * @param record
	 * @param manager 更新部門資料
	 * @throws Exception
	 */
	public void updateDepartment(TbDepartment record, String manager) throws Exception;

	/**
	 * @param record 刪除部門資料
	 * @throws Exception
	 */
	public void delDepartment(TbDepartment record) throws Exception;

	/**
	 * @param depId
	 * @return Set<String> 依照部門ID，取得所有子部門集合(包含自身)
	 * @throws Exception
	 */
	public Set<String> getChildDepartments(String depId) throws Exception;

	/**
	 * @param depId
	 * @return Set<String> 依照部門ID，取得其父類部門
	 * @throws Exception
	 */
	public Set<String> getParentDepartments(String depId) throws Exception;

	/**
	 * @param id
	 * @return TbDepartment 依照ID，取得部門資料
	 * @throws Exception
	 */
	public TbDepartment findDepartmentById(String id) throws Exception;

	/**
	 * @param ids
	 * @return List<TbDepartment> 依照ID集合，取得部門表單
	 * @throws Exception
	 */
	public List<TbDepartment> getDepListByIds(List<Integer> ids) throws Exception;
}
