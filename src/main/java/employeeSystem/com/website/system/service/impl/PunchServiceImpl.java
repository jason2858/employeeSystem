package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.dao.HolidayEventDao;
import com.yesee.gov.website.dao.PunchRecordsDao;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbHolidayEvent;
import com.yesee.gov.website.model.TbPunchRecords;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.PreferenceService;
import com.yesee.gov.website.service.PunchService;

@Service("punchService")
public class PunchServiceImpl implements PunchService {

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PunchRecordsDao punchRecordsDao;

	@Autowired
	private HolidayEventDao holidayEventDao;

	@Autowired
	private SchedulesDao schedulesDao;

	@Override
	public List<TbPunchRecords> getPunchStatus(String user) throws Exception {
		List<TbPunchRecords> list = punchRecordsDao.findPunchStatus(user, "out");
		if (list.size() != 0) {
			return list;
		} else {
			list = punchRecordsDao.findPunchStatus(user, "in");
			return list;
		}
	}

	@Override
	public List<TbPunchRecords> getMakeUpCount(String user, String startDate, String endDate) throws Exception {
		return punchRecordsDao.findMakeUpCount(user, startDate, endDate);
	}

	@Override
	public List<TbPunchRecords> getRecords(String user, String startDate, String endDate) throws Exception {
		return punchRecordsDao.getRecords(user, startDate, endDate);
	}

	@Override
	public Map<String, Object> getHoliday(String startDate, String endDate) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<?> list = holidayEventDao.findHolidayByDates(startDate, endDate);
		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			TbHolidayEvent record = (TbHolidayEvent) it.next();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(record.getStartDate());
			map.put(date + "type", record.getHolidayType());
			map.put(date + "name", record.getHolidayName());
		}
		return map;
	}

	public String switchType(int getType) {
		String type = "未定義";
		switch (getType) {
		case 2:
			type = "出差";
			break;
		case 3:
			type = "特休";
			break;
		case 4:
			type = "事假";
			break;
		case 5:
			type = "病假";
			break;
		case 6:
			type = "公假";
			break;
		case 7:
			type = "婚假";
			break;
		case 8:
			type = "喪假";
			break;
		case 9:
			type = "加班";
			break;
		case 10:
			type = "補休";
			break;
		case 11:
			type = "外出";
			break;
		}
		return type;
	}

	@Override
	public Map<String, Object> getAttendance(String user, String startDate, String endDate) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		List<TbSchedules> list = schedulesDao.findScheduleByDate(user, "SIGNED", startDate, endDate);
		Iterator<TbSchedules> it = list.iterator();
		Date start = sd.parse(startDate);
		String type, detail;
		int year, month, startDay, endDay;
		year = start.getYear() + 1900;
		month = start.getMonth() + 1;
		String date;
		while (it.hasNext()) {
			TbSchedules record = it.next();
			type = switchType(record.getType());
			detail = "自 " + sdf.format(record.getStartTime()) + " 至 " + sdf.format(record.getEndTime()) + " 已有";
			if (record.getType() != 2) {
				detail += "共 " + record.getAnnualLeaveTimes() + " 小時之";
			}
			detail += " " + type + " 紀錄";
			if ((record.getStartTime().getMonth() + 1) < month) {
				startDay = 1;
			} else {
				startDay = record.getStartTime().getDate();
			}
			if ((record.getEndTime().getMonth() + 1) > month) {
				cal.setTime(record.getEndTime());
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.DATE, -1);
				endDay = cal.getTime().getDate();
			} else
				endDay = record.getEndTime().getDate();
			if (month < 10) {
				date = year + "-0" + month;
			} else {
				date = year + "-" + month;
			}
			for (int i = startDay; i <= endDay; i++) {
				if (i < 10) {
					map.put(date + "-0" + i, detail);
				} else {
					map.put(date + "-" + i, detail);
				}
			}
		}
		return map;
	}

	@Override
	public int punch(TbPunchRecords record) throws Exception {
		int result = 0;
		List<TbPunchRecords> check = punchRecordsDao.findPunch(record);
		punchRecordsDao.save(record);
		if (check.size() == 0) {
			result = 1;
		} else if (record.getType().equals("in")) {
			result = 2;
		} else {
			result = 3;
		}
		return result;
	}

	@Override
	public int makeUp(TbPunchRecords record) throws Exception {
		int result = 0;
		Date date = new Date();
		if (date.before(record.getPunchTime())) {
			result = 2;
		} else {
			List<TbPunchRecords> check = punchRecordsDao.findMakeUp(record);
			if (check.size() == 0) {
				punchRecordsDao.save(record);
				result = 1;
			}
		}
		return result;
	}

	@Override
	public int del(Long id) throws Exception {
		int result = 0;
		TbPunchRecords record = punchRecordsDao.findById(id);
		if (record.getTbEmployeesBySigner() == null) {
			punchRecordsDao.del(id);
			result = 1;
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getUnsignedMakeUpRecords(String nameSelect, String depId, String authorise,
			String account) throws Exception {
		List<TbEmployees> employees = accountService.getSubordinateList(authorise, depId, null);
		List<TbDepartment> depList = departmentDao.getList();
		Map<Integer, Object> department = new HashMap<>();
		for (int i = 0; i < depList.size(); i++) {
			department.put(depList.get(i).getId(), depList.get(i).getName());
		}
		Map<String, Object> depForEmp = new HashMap<>();
		for (int i = 0; i < employees.size(); i++) {
			depForEmp.put(employees.get(i).getUsername(),
					department.get(Integer.parseInt(employees.get(i).getDepartmentId())));
		}
		List<String> nameList = new ArrayList<String>();
		if (authorise.equals("3")) {
			nameList = employees.stream().filter(x -> !account.equals(x.getUsername())).map(TbEmployees::getUsername)
					.collect(Collectors.toList());
		} else {
			nameList = employees.stream().map(TbEmployees::getUsername).collect(Collectors.toList());
		}
		List<TbPunchRecords> list = new ArrayList<TbPunchRecords>();
		if (!nameList.isEmpty()) {
			list = punchRecordsDao.findAllMakeUpByStatus(nameList, "CREATED");
		}
		Iterator<TbPunchRecords> it = list.iterator();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		List<Map<String, Object>> result = new ArrayList<>();
		String punchTime, time;
		while (it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			TbPunchRecords record = it.next();
			punchTime = df.format(record.getPunchTime());
			time = df.format(record.getCreatedAt());
			map.put("id", record.getId());
			if ("TW".equals(nameSelect)) {
				map.put("user", record.getTbEmployeesByUser().getChineseName());
			} else {
				map.put("user", record.getTbEmployeesByUser().getUsername());
			}
			map.put("department", depForEmp.get(record.getTbEmployeesByUser().getUsername()));
			map.put("type", record.getType());
			map.put("punchTime", punchTime);
			map.put("time", time);
			map.put("latitude", record.getLatitude());
			map.put("longitude", record.getLongitude());
			map.put("note", record.getNote());
			result.add(map);
		}
		return result;
	}

	@Override
	public int checkMakeUpAndUpdate(String account, String id, String status, String reason) throws Exception {
		int check = 1;
		TbPunchRecords record = punchRecordsDao.findById(Long.parseLong(id));
		if (!record.getStatus().equals("CREATED") || record.getTbEmployeesBySigner() != null) {
			check = 0;
		} else {
			updateMakeUpStatus(account, record, status, reason);
		}
		return check;
	}

	@Override
	public void updateMakeUpStatus(String account, TbPunchRecords record, String status, String reason)
			throws Exception {
		record.setTbEmployeesBySigner(new TbEmployees());
		record.getTbEmployeesBySigner().setUsername(account);
		record.setSignedAt(new Date());
		record.setStatus(status);
		if (status.equals("REJECTED")) {
			record.setNote(record.getNote() + " ,駁回原因：" + reason);
		}
		punchRecordsDao.save(record);
	}

	@Override
	public List<Map<String, Object>> getMakeUpRecords(String nameSelect, String status, String depId, String authorise,
			String account, String startDate, String endDate) throws Exception {
		List<TbEmployees> employees = accountService.getSubordinateList(authorise, depId, null);
		List<TbDepartment> depList = departmentDao.getList();
		Map<Integer, Object> department = new HashMap<>();
		for (int i = 0; i < depList.size(); i++) {
			department.put(depList.get(i).getId(), depList.get(i).getName());
		}
		Map<String, Object> depForEmp = new HashMap<>();
		for (int i = 0; i < employees.size(); i++) {
			depForEmp.put(employees.get(i).getUsername(),
					department.get(Integer.parseInt(employees.get(i).getDepartmentId())));
		}
		List<String> nameList = new ArrayList<String>();
		if (authorise.equals("3")) {
			nameList = employees.stream().filter(x -> !account.equals(x.getUsername())).map(TbEmployees::getUsername)
					.collect(Collectors.toList());
		} else {
			nameList = employees.stream().map(TbEmployees::getUsername).collect(Collectors.toList());
		}
		List<TbPunchRecords> list = new ArrayList<TbPunchRecords>();
		if (!nameList.isEmpty()) {
			list = punchRecordsDao.findMakeUpByStatus(nameList, status, startDate, endDate);
		}
		Iterator<TbPunchRecords> it = list.iterator();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> result = new ArrayList<>();
		String punchTime, time;
		while (it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			TbPunchRecords record = it.next();
			punchTime = df.format(record.getPunchTime());
			time = df.format(record.getCreatedAt());
			String note = record.getNote();
			String reason = "";
			if (note.contains(",駁回原因：")) {
				reason = note.split(",駁回原因：")[1];
				note = note.split(",駁回原因：")[0];
			}
			map.put("id", record.getId());
			if ("TW".equals(nameSelect)) {
				if (record.getTbEmployeesByUser() != null) {
					map.put("user", record.getTbEmployeesByUser().getChineseName());
				}
				if (record.getTbEmployeesBySigner() != null) {
					map.put("signer", record.getTbEmployeesBySigner().getChineseName());
				}
			} else {
				if (record.getTbEmployeesByUser() != null) {
					map.put("user", record.getTbEmployeesByUser().getUsername());
				}
				if (record.getTbEmployeesBySigner() != null) {
					map.put("signer", record.getTbEmployeesBySigner().getUsername());
				}
			}
			map.put("department", depForEmp.get(record.getTbEmployeesByUser().getUsername()));
			map.put("type", record.getType());
			map.put("punchTime", punchTime);
			map.put("time", time);
			map.put("latitude", record.getLatitude());
			map.put("longitude", record.getLongitude());
			map.put("reason", reason);
			map.put("note", note);
			map.put("signTime", d.format(record.getSignedAt()));
			result.add(map);
		}
		return result;
	}
}