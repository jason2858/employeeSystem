package employeeSystem.com.website.system.dao;

import java.util.List;

import employeeSystem.com.website.system.model.TbProject;

public interface ProjectDao {
	/**
	 * @param project
	 * @throws Exception 將此筆TbProject project存入資料庫
	 */
	public void save(TbProject project) throws Exception;

	/**
	 * @param project
	 * @throws Exception 將符合此筆TbProject project的資料從資料庫中刪除
	 */
	public void delete(TbProject project) throws Exception;

	/**
	 * @param owner
	 * @return
	 * @throws Exception 取出TbProject中pm符合owner的資料
	 */
	public List<TbProject> getList(String owner) throws Exception;

	/**
	 * @return
	 * @throws Exception 取出TbProject內的所有資料
	 */
	public List<TbProject> getAllList() throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception 取出TbProject內值為id的project
	 */
	public TbProject get(Integer id) throws Exception;

	/**
	 * @return
	 * @throws Exception 取出TbProject內devStatus符合unsign的資料
	 */
	public Integer getUnsignCount() throws Exception;

}