package com.yesee.gov.website.service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.VTbSchedules;
import com.yesee.gov.website.pojo.Attendance;
import com.yesee.gov.website.pojo.InsertAttendanceInfo;
import com.yesee.gov.website.pojo.UpdateAttendanceInfo;
public interface AttendanceService {
	/**
	 * @param nameSelect
	 * @param authorise
	 * @param account
	 * @param startTime
	 * @param endTime
	 * @return List<Attendance>
	 * 取得拆勤資料
	 * @throws Exception
	 */
	public List<Attendance> getAttendanceInfo(String nameSelect, int authorise, String account, String startTime,
			String endTime) throws Exception;
	/**
	 * @param insertAttendanceInfo
	 * @param account
	 * @return int
	 * 儲存差勤後返回差勤ID
	 * @throws Exception
	 */
	public int transAttInfoToObjAndInsert(InsertAttendanceInfo insertAttendanceInfo, String account) throws Exception;
	/**
	 * @param id
	 * 寄送郵件
	 * @throws Exception
	 */
	public void sendEmail(int id) throws Exception;
	/**
	 * @param updateAttendanceInfo
	 * @param account
	 * 更新差勤
	 * @throws Exception
	 */
	public void transAttInfoToObjAndUpdate(UpdateAttendanceInfo updateAttendanceInfo, String account) throws Exception;
	/**
	 * @param id
	 * 刪除差勤
	 * @throws Exception
	 */
	public void setAttInfoToObjAndDelete(String id) throws Exception;
	/**
	 * @param account
	 * @param entity
	 * @return List<TbEmployees>
	 * 依照帳號取得員工表
	 * @throws Exception
	 */
	public List<TbEmployees> getEmployees(String account, TbEmployees entity) throws Exception;
	/**
	 * @param account
	 * @param year
	 * @return String
	 * 依照帳號取得年度特休時數
	 * @throws Exception
	 */
	public String getAnnualLeave(String account, String year) throws Exception;
	/**
	 * @param account
	 * @return String
	 * 依照帳號，取得年度特休時數
	 * @throws Exception
	 */
	public String getAnnualLeaveInfo(String account) throws Exception;
	/**
	 * @param id
	 * @return int
	 * 取得簽核及駁回的數量
	 * @throws Exception
	 */
	public int check(String id) throws Exception;
	/**
	 * @param nameSelect
	 * @param depId
	 * @param authorise
	 * @param account
	 * @return List<Map<String, Object>>
	 * 取得未簽核差勤明細
	 * @throws Exception
	 */
	public List<Map<String, Object>> getUnsignedAttRecords(String nameSelect, String depId, String authorise,
			String account) throws Exception;
	/**
	 * @param account
	 * @param id
	 * @param updateAt
	 * @param status
	 * @param reason
	 * @return int
	 * 簽核前確認資料是否有被修改
	 * @throws Exception
	 */
	public int checkScheduleAndUpdate(String account, String id, String updateAt, String status, String reason)
			throws Exception;
	/**
	 * @param account
	 * @param record
	 * @param status
	 * @param reason
	 * 更新差勤狀態
	 * @throws Exception
	 */
	public void updateScheduleStatus(String account, TbSchedules record, String status, String reason) throws Exception;
	/**
	 * @param nameSelect
	 * @param status
	 * @param depId
	 * @param authorise
	 * @param account
	 * @param startDate
	 * @param endDate
	 * @return List<Map<String, Object>>
	 * 取得差勤明細
	 * @throws Exception
	 */
	public List<Map<String, Object>> getAttRecords(String nameSelect, String status, String depId, String authorise,
			String account, String startDate, String endDate) throws Exception;
	/**
	 * @param nameSelect
	 * @param depId
	 * @param authorise
	 * @param account
	 * @param year
	 * @return List<Map<String, Object>>
	 * 取得年度差勤明細表
	 * @throws Exception
	 */
	public List<Map<String, Object>> getYearsAttRecords(String nameSelect, String depId, String authorise,
			String account, int year) throws Exception;
	/**
	 * @param name
	 * @param type
	 * @param year
	 * @return List<TbSchedules>
	 * 取得差勤狀態一覽明細
	 * @throws Exception
	 */
	public List<TbSchedules> getYearDetail(String name, String type, int year) throws Exception;
	/**
	 * @param createdAt
	 * @return String
	 * 取得差勤編號
	 * @throws Exception
	 */
	public String getSerialNumber(Date createdAt) throws Exception;

	/**
	 * @param user
	 * @return List<VTbSchedules>
	 * 取得補休時數一覽明細
	 * @throws Exception
	 */
	public List<VTbSchedules> getAvailableTimeList(String user) throws Exception;
}
