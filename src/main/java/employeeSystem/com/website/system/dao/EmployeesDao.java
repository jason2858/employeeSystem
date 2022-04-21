package employeeSystem.com.website.system.dao;

import java.util.List;

import employeeSystem.com.website.system.model.TbEmployees;

public interface EmployeesDao {

	/**
	 * @param entity
	 * @return
	 * @throws Exception 可以依entity內的各個key值找出TbEmployees內符合的資料
	 */
	public List<TbEmployees> getList(TbEmployees entity) throws Exception;

	/**
	 * @param name
	 * @return
	 * @throws Exception 取出TbEmployees內username符合name的資料
	 */
	public List<TbEmployees> findByUserName(String name) throws Exception;

	/**
	 * @param ids
	 * @param entity
	 * @return
	 * @throws Exception 取出TbEmployees內departmentId符合ids且可依entity內的key值做額外判斷
	 */
	public List<TbEmployees> findBydepId(List<String> ids, TbEmployees entity) throws Exception;

	/**
	 * @param object
	 * @throws Exception 將符合此筆TbEmployees object的資料從資料庫刪除
	 */
	public void delete(TbEmployees object) throws Exception;

	/**
	 * @param object
	 * @throws Exception 將此筆TbEmployees object存入資料庫
	 */
	public void save(TbEmployees object) throws Exception;

	/**
	 * @param account
	 * @return
	 * @throws Exception 取出TbEmployees內username不等於account並且departmentId為15的資料
	 */
	public List<TbEmployees> findHR(String account) throws Exception;

}