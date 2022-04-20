package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbAnnounce;

public interface AnnounceDao {

	/**
	 * @param object
	 * @throws Exception 將此筆TbAnnounce object存入資料庫
	 */
	public void save(TbAnnounce object) throws Exception;

	/**
	 * @param object
	 * @throws Exception 將符合此筆TbAnnounce object的資料從資料庫中刪除
	 */
	public void delete(TbAnnounce object) throws Exception;

	/**
	 * @return
	 * @throws Exception 取出TbAnnounce所有資料，以id欄位降序排列
	 */
	public List<TbAnnounce> getList(String type) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception 取出TbAnnounce內符合id的資料若無則回傳null
	 */
	public TbAnnounce getAnnounceById(Integer id) throws Exception;

	/**
	 * @param companyId
	 * @return
	 * @throws Exception 取出TbAnnounce內符合companyId的資料，以id欄位降序排列
	 */
	public List<TbAnnounce> getListByCompanyId(String companyId, String type) throws Exception;

	/**
	 * @param id
	 * @param companyId
	 * @return
	 * @throws Exception 取出TbAnnounce內資料小於id且符合companyId的資料，以id欄位降序排列
	 */
	public List<TbAnnounce> getListMoreThanIdAndCompanyId(Integer id, String companyId) throws Exception;
}