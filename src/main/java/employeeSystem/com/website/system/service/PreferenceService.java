package employeeSystem.com.website.system.service;

import java.util.List;

import employeeSystem.com.website.system.model.TbPreference;

public interface PreferenceService {

	public static final String Key_Personal_Homepage = "personal_homepage";
	public static final String Key_NameSelect = "nameSelect";
	public static final String Key_PunchOutRemindHour = "punchOutRemindHour";
	public static final String Key_RemindPunchOut = "remindPunchOut";

	/**
	 * @param object 新增偏好設定
	 * @throws Exception
	 */
	public void save(TbPreference object) throws Exception;

	/**
	 * @param object 刪除偏好設定
	 * @throws Exception
	 */
	public void del(TbPreference object) throws Exception;

	/**
	 * @param user
	 * @param key
	 * @return TbPreference 依照帳號及key，取得偏好設定
	 * @throws Exception
	 */
	public TbPreference getByUserAndKey(String user, String key) throws Exception;

	/**
	 * @param user
	 * @return List<TbPreference> 依照帳號，取得偏好設定表
	 * @throws Exception
	 */
	public List<TbPreference> getByUser(String user) throws Exception;

}