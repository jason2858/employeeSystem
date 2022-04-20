package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbAClassItem;

public interface AClassItemDao {

	/**
	 * @param c_id
	 * @param i_id   模糊查詢
	 * @param i_name 模糊查詢
	 * @return List<AClassItem>
	 * @throws Exception 取出AClassItem內符合搜尋結果的資料
	 */
	public List<TbAClassItem> getList(Map<String, Object> param) throws Exception;

	/**
	 * @param i_id
	 * @throws Exception 將此筆AClassItem aClassItem從資料庫刪除
	 */
	public TbAClassItem findById(String id) throws Exception;

	/**
	 * @param aClassItem
	 * @throws Exception 將此筆AClassItem aClassItem存入資料庫或在資料庫上更新
	 */
	public void save(TbAClassItem aClassItem) throws Exception;

	public void update(TbAClassItem aClassItem) throws Exception;

	public String getAClassItemType(String item) throws Exception;

}
