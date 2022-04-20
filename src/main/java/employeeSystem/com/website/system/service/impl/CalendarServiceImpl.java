package com.yesee.gov.website.service.impl;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.dao.HolidayEventDao;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbHolidayEvent;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.pojo.Event;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.CalendarService;
import com.yesee.gov.website.service.DepartmentService;
import com.yesee.gov.website.service.EmployeesService;
import com.yesee.gov.website.service.PreferenceService;
import com.yesee.gov.website.util.HolidayEvent;

@Service("calendarService")
public class CalendarServiceImpl implements CalendarService {

	private static final Logger logger = LogManager.getLogger(CalendarServiceImpl.class);

	@Autowired
	private HolidayEventDao holidayEventDao;

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EmployeesDao employeesDao;

	@Autowired
	private EmployeesService employeesService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private PreferenceService preferenceService;

	@Override
	public String chgCallendarInfoToEvent(String nameSelect, int authorise, String account, String depId)
			throws Exception {
		String jsonMsg;
		Event event = null;
		// 裝載國定假日，往後若改成資料庫輸出要另行修改
		List<Event> holidayEvents = new ArrayList<Event>();

		// 資料庫輸出國定假日
		List<TbHolidayEvent> eIist = holidayEventDao.getAll();
		if (!CollectionUtils.isEmpty(eIist)) {
			eIist.forEach(i -> {
				Event e = new Event();
				e.setDescription(i.getHolidayName());
				e.setTitle(" ");// 暫未製定國定假日的title
				if (i.getStartDate() != null) {
					e.setStart(i.getStartDate().toString());
				}
				e.setType(i.getHolidayType());
				holidayEvents.add(e);
			});
		}

		List<TbSchedules> list = getSchedules(authorise, account, depId);

		Iterator<TbSchedules> it = list.iterator();

		List<Event> events = new ArrayList<Event>();

		logger.info("get calendarinfo size = " + list.size());
		// ->event object
		while (it.hasNext()) {
			TbSchedules tbSchedules = it.next();
			event = new Event();
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String status = "";
			if ("CREATED".equals(tbSchedules.getStatus()) || "MODIFIED".equals(tbSchedules.getStatus())) {
				status = "(未簽核)";
			}else if("WAIT_DELETE".equals(tbSchedules.getStatus())) {
				status = "(待刪除)";
			}
			String StartTime = sdf.format(tbSchedules.getStartTime());
			String EndTime = sdf.format(tbSchedules.getEndTime());
			StringBuilder note = new StringBuilder(tbSchedules.getTbEmployeesByUser().getUsername()).append("<br>")
					.append(tbSchedules.returnTypeString()).append(status).append("<br>起始日期:<br>").append(StartTime)
//			StringBuilder note = new StringBuilder(tbSchedules.getTbEmployeesByUser().getUsername())
//					.append(tbSchedules.returnTypeString()).append("<br>起始日期:<br>").append(StartTime)
					.append("<br>結束日期:<br>").append(EndTime).append("<br>備註:<br>").append(tbSchedules.getNote());
			if (tbSchedules.getTbEmployeesByDeputy() != null) {
				if("TW".equals(nameSelect)){
					note.append("<br>代理人:<br>").append(tbSchedules.getTbEmployeesByDeputy().getChineseName());
				}else {
					note.append("<br>代理人:<br>").append(tbSchedules.getTbEmployeesByDeputy().getUsername());
				}
			}
			if (tbSchedules.getAnnualLeaveTimes() != 0) {
				note.append("<br>特休時數:<br>").append(tbSchedules.getAnnualLeaveTimes()).append("小時");
			}

			if ("TW".equals(nameSelect)) {
				event.setTitle(tbSchedules.getTbEmployeesByUser().getChineseName() + " " + tbSchedules.returnTypeString() + status);
				if (tbSchedules.getStatus().equals("CREATED")) {
					event.setTitle("*" + tbSchedules.getTbEmployeesByUser().getChineseName());
				} else {
					event.setTitle(tbSchedules.getTbEmployeesByUser().getChineseName());
				}
			} else {
				event.setTitle(tbSchedules.getTbEmployeesByUser().getUsername() + " " + tbSchedules.returnTypeString() + status);

				if (tbSchedules.getStatus().equals("CREATED")) {
					event.setTitle("*" + tbSchedules.getTbEmployeesByUser().getUsername());
				} else {
					event.setTitle(tbSchedules.getTbEmployeesByUser().getUsername());
				}
			}

			event.setDescription(tbSchedules.returnTypeString());
			event.setStart(tbSchedules.getStartTime().toString());
			event.setEnd(tbSchedules.getEndTime().toString());
			event.setNote(note.toString());
			events.add(event);
		}

		events.addAll(holidayEvents);
		ObjectMapper mapper = new ObjectMapper();
		jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);

		return jsonMsg;
	}

	private List<TbSchedules> getSchedules(int authorise, String emp, String depId) throws Exception {

		List<TbSchedules> list = new ArrayList<>();
		List<String> empsForNor = new ArrayList<>();
		empsForNor.add(emp);
		if (authorise == 1) {
			list = schedulesDao.getList().stream()
					.filter(s -> ("SIGNED".equals(s.getStatus()) || "CREATED".equals(s.getStatus())))
					.collect(Collectors.toList());
		} else if (authorise == 2) {
			String companyId = departmentService.findDepartmentById(depId).getCompanyId();
			List<TbEmployees> emps = employeesService.getEmployeesByCompany(companyId, null);
			List<TbSchedules> tmpList = schedulesDao
					.getSchedulesByEmployees(emps.stream().map(TbEmployees::getUsername).collect(Collectors.toList()));
			list = tmpList.stream().filter(s -> ("SIGNED".equals(s.getStatus()) || "CREATED".equals(s.getStatus())))
					.collect(Collectors.toList());
		} else if ("4".equals(authorise + "")) {// 一般員工撈自己的行程
			list = schedulesDao.getSchedulesByEmployees(empsForNor).stream()
					.filter(s -> ("SIGNED".equals(s.getStatus()) || "CREATED".equals(s.getStatus())))
					.collect(Collectors.toList());
		} else if ("3".equals(authorise + "")) {// 撈出主管以下成員所有行程
			Map<?, ?> depMap = accountService.getDepList(emp);

			List<TbEmployees> emps = employeesDao.findBydepId(
					depMap.entrySet().stream().map(e -> e.getKey() + "").collect(Collectors.toList()), null);
			List<TbSchedules> tmpList = schedulesDao
					.getSchedulesByEmployees(emps.stream().map(TbEmployees::getUsername).collect(Collectors.toList()));

			list = tmpList.stream().filter(s -> ("SIGNED".equals(s.getStatus()) || "CREATED".equals(s.getStatus())))
					.collect(Collectors.toList());
		}

		return list;
	}

	@Override
	public void uploadHoliday() throws Exception {
		JSONObject j;
		// 取得google calendar
		InputStream is = new URL(
				"https://www.googleapis.com/calendar/v3/calendars/zh.taiwan%23holiday%40group.v.calendar.google.com/events?key=AIzaSyC81zDx9vstPlgz6PWw_QVLLfWyZH7ubnQ")
						.openStream();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String jsonTxt = s.hasNext() ? s.next() : "";
		j = new JSONObject(jsonTxt);

		j.getJSONArray("items").forEach(item -> {
			JSONObject obj = (JSONObject) item;
			String k = obj.keys().next();
			for (HolidayEvent value : HolidayEvent.values()) {
				if (k.equals("summary") && obj.get("summary").toString().contains(value.holiday())) {
					JSONObject sObj = (JSONObject) obj.get("start");
					try {
						holidayEventDao.save(obj.get("summary").toString(), sObj.get("date").toString(), null);
					} catch (Exception e1) {
						logger.error(e1);
					}
				}
			}
		});
		is.close();
		s.close();
	}

}
