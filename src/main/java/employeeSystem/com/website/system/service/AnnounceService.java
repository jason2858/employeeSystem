package employeeSystem.com.website.system.service;

import java.util.List;

import employeeSystem.com.website.system.model.TbAnnounce;
import employeeSystem.com.website.system.model.TbAnnounceDoc;

public interface AnnounceService {

	/**
	 * @param authorise
	 * @param companyId
	 * @return List<TbAnnounce> 取得公告
	 * @throws Exception
	 */
	public List<TbAnnounce> getRecords(Integer authorise, String companyId, String type) throws Exception;

	/**
	 * @param AnnounceId
	 * @return List<TbAnnounceDoc> 依據ID，取得公告文件資料列
	 * @throws Exception
	 */
	public List<TbAnnounceDoc> getRecordsByAnnounceId(Integer AnnounceId) throws Exception;

	/**
	 * @param type
	 * @param object 更新公告
	 * @throws Exception
	 */
	public void update(TbAnnounce object) throws Exception;

	/**
	 * @param id 依據公告ID取得公告
	 * @throws Exception
	 */
	public TbAnnounce getAnnounceById(Integer id) throws Exception;

	/**
	 * @param object 更新公告文件
	 * @throws Exception
	 */
	public void updateDoc(TbAnnounceDoc object) throws Exception;

	/**
	 * @param docId 刪除公告文件
	 * @throws Exception
	 */
	public void deleteDoc(Integer docId) throws Exception;

	/**
	 * @param docId
	 * @return String 依據文件ID，取得路徑
	 * @throws Exception
	 */
	public String getFilePath(Integer docId) throws Exception;

	/**
	 * @param id 刪除公告
	 * @throws Exception
	 */
	public void delete(Integer id) throws Exception;

	/**
	 * @param id
	 * @param companyId
	 * @return List<TbAnnounce> 依據公司以及公告ID，取得公告列表
	 * @throws Exception
	 */
	public List<TbAnnounce> getUnreadAnnounce(Integer id, String companyId) throws Exception;
}