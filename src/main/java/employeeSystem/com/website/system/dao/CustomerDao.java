package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbCustomer;

public interface CustomerDao {
	
	/**
	 * @param customer
	 * @throws Exception
	 *  將此筆TbCustomer customer存入資料庫
	 */
	public void save(TbCustomer customer) throws Exception;
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 * 取出TbCustomer內符合id的資料若無則回傳null
	 */
	public TbCustomer findById(Integer id) throws Exception;

	/**
	 * @param customer
	 * @throws Exception
	 * 將符合此筆TbCustomer customer的資料從資料庫刪除
	 */
	public void delete(TbCustomer customer) throws Exception;
	
	public void removeParentId(Integer id) throws Exception;
	
	/**
	 * @param account
	 * @return
	 * @throws Exception
	 * 取出TbCustomer內status為signed的資料或是同時符合creator是account與status不等於delete的資料
	 */
	public List<TbCustomer> getVisibleList(String account) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 * 取出TbCustomer內未簽核的資料數量
	 */
	public Integer getUnsignCount()throws Exception;
	
	/**
	 * @param status
	 * @return
	 * @throws Exception
	 * 取出TbCustomer內不等於status的資料
	 */
	public List<TbCustomer> getListByNotEqualsStatus(String status) throws Exception;
	
	public List<TbCustomer> getListByStatus(String status) throws Exception;
	
	public TbCustomer findByName(String name) throws Exception ;

	public TbCustomer findByEin(String cusTaxId) throws Exception;

}