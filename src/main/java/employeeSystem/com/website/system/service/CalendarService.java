package com.yesee.gov.website.service;

public interface CalendarService {
	
	/**
	 * @param nameSelect
	 * @param authorise
	 * @param account
	 * @param depId
	 * @return String
	 * 取得行事曆所有事件
	 * @throws Exception
	 */
	public String chgCallendarInfoToEvent(String nameSelect, int authorise, String account, String depId) throws Exception;
	
	/**
	 * 下載google前後兩年國定假日行事曆
	 * @throws Exception
	 */
	public void uploadHoliday() throws Exception;
	
}
