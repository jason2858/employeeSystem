package com.yesee.gov.website.dao;

import java.util.List;

import com.yesee.gov.website.model.TbHolidayEvent;

public interface HolidayEventDao {

	/**
	 * @param holidayName
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 * 將holidayName,startDate,endDate資料存入TbHolidayEvent
	 */
	public void save(String holidayName, String startDate, String endDate) throws Exception;

	/**
	 * @param holidayName
	 * @param startDate
	 * @return
	 * @throws Exception
	 * 取出TbHolidayEvent內holiday_name符合holidayName且start_date符合startDate的資料
	 */
	public List<TbHolidayEvent> get(String holidayName, String startDate) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 * 取出TbHolidayEvent內的所有資料
	 */
	public List<TbHolidayEvent> getAll() throws Exception;

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 * 取出TbHolidayEvent中日期介於startDate與endDate中的資料
	 */
	public List<TbHolidayEvent> findHolidayByDates(String startDate, String endDate) throws Exception;

}