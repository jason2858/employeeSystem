package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbAnnounce;
import com.yesee.gov.website.model.TbAnnounceDoc;

public interface AnnounceDocDao {

	/**
	 * @param object
	 * @throws Exception
	 * 將此筆TbAnnounceDoc object存入資料庫
	 */
	public void save(TbAnnounceDoc object) throws Exception;

	/**
	 * @param AnnounceId
	 * @throws Exception
	 * 將符合AnnounceId的資料從資料庫刪除
	 */
	public void deleteDocByAnnounceId(Integer AnnounceId) throws Exception;

	/**
	 * @param docId
	 * @return
	 * @throws Exception
	 * 取出符合docId的資料若無則回傳null
	 */
	public TbAnnounceDoc getDocById(Integer docId) throws Exception;

	/**
	 * @param record
	 * @throws Exception
	 * 將符合TbAnnounceDoc object的資料從資料庫刪除
	 */
	public void deleteByDocId(TbAnnounceDoc record) throws Exception;

	/**
	 * @param AnnounceId
	 * @return
	 * @throws Exception
	 * 取出符合AnnounceId的資料
	 */
	public List<TbAnnounceDoc> getRecordsByAnnounceId(Integer AnnounceId) throws Exception;

}