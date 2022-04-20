package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbCompany;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.pojo.ProjectVO;

public interface CustomerService {

	/**
	 * @param account
	 * @param authorise
	 * @return List<TbCustomer>
	 * 取得非刪除的客戶資料
	 * @throws Exception
	 */
	public List<TbCustomer> getList(String account, String authorise) throws Exception;

	/**
	 * @param customer
	 * @param account
	 * 儲存客戶資料
	 * @throws Exception
	 */
	public void save(TbCustomer customer, String account) throws Exception;
	
	/**
	 * @param customer
	 * @return
	 * 檢查客戶資料是否已更新
	 * @throws Exception
	 */
	public TbCustomer checkUpdate(TbCustomer customer) throws Exception;
	
	/**
	 * @param object
	 * 更新客戶資料
	 * @throws Exception
	 */
	public void update(TbCustomer customer, TbCustomer object) throws Exception;

	/**
	 * @param id
	 * @return TbCustomer
	 * 依照ID找出客戶資料
	 * @throws Exception
	 */
	public TbCustomer findById(Integer id) throws Exception;

	/**
	 * @param object
	 * 刪除客戶
	 * @throws Exception
	 */
	public void delete(TbCustomer object) throws Exception;

	/**
	 * @param object
	 * 簽核客戶申請
	 * @throws Exception
	 */
	public void sign(TbCustomer object) throws Exception;

}
