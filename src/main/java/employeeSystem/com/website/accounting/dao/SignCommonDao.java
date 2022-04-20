package com.yesee.gov.website.dao.accounting;

import java.util.List;

import com.yesee.gov.website.model.accounting.TbSignCommon;

public interface SignCommonDao {

	/**
	 * @throws Exception 取得TbSignCommon內所有資料
	 */
	public List<TbSignCommon> getSignCommonList() throws Exception;

	/**
	 * @param signName
	 * @return
	 * @throws Exception 取出TbSignCommon內符合signName的資料若無則回傳null
	 */
	public List<TbSignCommon> getSignCommon(String signName) throws Exception;

	/**
	 * @param tbSignCommon
	 * @throws Exception 儲存TbSignCommon
	 */
	public void save(TbSignCommon tbSignCommon) throws Exception;

	public void update(TbSignCommon tbSignCommon) throws Exception;
}
