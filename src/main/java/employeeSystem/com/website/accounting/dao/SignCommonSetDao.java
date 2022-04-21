package employeeSystem.com.website.accounting.dao;

import java.util.List;

import employeeSystem.com.website.accounting.model.TbSignCommonSet;

public interface SignCommonSetDao {
	/**
	 * @param tbSignCommonSet
	 * @throws Exception 儲存TbSignCommonSet資料
	 */
	public void save(TbSignCommonSet tbSignCommonSet) throws Exception;

	public void update(TbSignCommonSet tbSignCommonSet) throws Exception;

	/**
	 * @throws Exception 取出TbSignCommonSet內符合signName的資料 若無則回傳null
	 */
	public List<TbSignCommonSet> getSignCommonSet(String signName) throws Exception;

}
