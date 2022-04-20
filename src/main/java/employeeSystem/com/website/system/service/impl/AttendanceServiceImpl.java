package com.yesee.gov.website.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.AnnualLeaveDao;
import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.dao.RejectedCodeDao;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.dao.SignCodeDao;
import com.yesee.gov.website.model.TbAnnualLeave;
import com.yesee.gov.website.model.TbAnnualLeaveId;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbRejectCode;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.TbSignCode;
import com.yesee.gov.website.model.VTbSchedules;
import com.yesee.gov.website.pojo.Attendance;
import com.yesee.gov.website.pojo.InsertAttendanceInfo;
import com.yesee.gov.website.pojo.UpdateAttendanceInfo;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.AttendanceService;
import com.yesee.gov.website.service.PreferenceService;
import com.yesee.gov.website.service.SendMailService;
import com.yesee.gov.website.service.SignService;
import com.yesee.gov.website.util.Config;
import com.yesee.gov.website.util.DateUtil;


@Service("attendanceService")
public class AttendanceServiceImpl implements AttendanceService {

	private static final Logger logger = LogManager.getLogger(AttendanceServiceImpl.class);

	@Autowired
	private AnnualLeaveDao annualLeaveDao;

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private EmployeesDao employeesDao;

	@Autowired
	private SignCodeDao signCodeDao;

	@Autowired
	private RejectedCodeDao rejectedCodeDao;

	@Autowired	
	private AccountService accountService;

	@Autowired
	private SignService signService;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private PreferenceService preferenceService;
	

	@Override
	public List<Attendance> getAttendanceInfo(String nameSelect, int authorise, String account, String startTime,
			String endTime) throws Exception {
		Attendance attendance = null;
		// 尾日加一天
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date eDate = sdf.parse(endTime);
		Calendar c = Calendar.getInstance();
		c.setTime(eDate);
		c.add(Calendar.DATE, 1);
		List<TbSchedules> list = schedulesDao.getSchedulesInfo(authorise, account, startTime, sdf.format(c.getTime()));
		Iterator<TbSchedules> it = list.iterator();
		List<Attendance> attendanceInfoList = new ArrayList<Attendance>();
		int countInfo = 0;
		while (it.hasNext()) {
			TbSchedules tbSchedules = it.next();
			attendance = new Attendance();
			// set createdAt format to disable mileseconds
			String createdAt = tbSchedules.getCreatedAt().toString();
			createdAt = createdAt.substring(0, createdAt.length() - 2);
			attendance.setCreatedAt(createdAt);
			String note = tbSchedules.getNote();
			String reason = "";
			if (note.contains(",駁回原因：")) {
				reason = note.split(",駁回原因：")[1];
				note = note.split(",駁回原因：")[0];
			}
			attendance.setNote(tbSchedules.getNote());
			attendance.setReason(reason);
			if (tbSchedules.getSignedAt() != null) {
				attendance.setSignAt(sdf.format(tbSchedules.getSignedAt()));
			} else {
				attendance.setSignAt("");
			}
			// set startTime format to disable mileseconds
			String startTimeView = tbSchedules.getStartTime().toString();
			startTimeView = startTimeView.substring(0, startTimeView.length() - 2);
			attendance.setStartTime(startTimeView);
			// set endTime format to disable mileseconds
			String endTimeView = tbSchedules.getEndTime().toString();
			endTimeView = endTimeView.substring(0, endTimeView.length() - 2);
			attendance.setEndTime(endTimeView);
			attendance.setStatus(tbSchedules.getStatus());
			attendance.setType(tbSchedules.returnTypeString());
			attendance.setId(tbSchedules.getId().toString());
			// set emp from nameSelect filter
			if ("TW".equals(nameSelect)) {
				attendance.setUser(tbSchedules.getTbEmployeesByUser().getChineseName());
				if (tbSchedules.getTbEmployeesBySigner() != null) {
					attendance.setSigner(tbSchedules.getTbEmployeesBySigner().getChineseName());
				}
				if (tbSchedules.getTbEmployeesByDeputy() == null) {
					attendance.setDeputy("none");
				} else {
					attendance.setDeputy(tbSchedules.getTbEmployeesByDeputy().getChineseName());
				}
			} else {
				attendance.setUser(tbSchedules.getTbEmployeesByUser().getUsername());
				if (tbSchedules.getTbEmployeesBySigner() != null) {
					attendance.setSigner(tbSchedules.getTbEmployeesBySigner().getUsername());
				}
				if (tbSchedules.getTbEmployeesByDeputy() == null) {
					attendance.setDeputy("none");
				} else {
					attendance.setDeputy(tbSchedules.getTbEmployeesByDeputy().getUsername());
				}
			}
			if (tbSchedules.getAnnualLeaveTimes() == null || tbSchedules.getAnnualLeaveTimes() == 0.0) {
				attendance.setAnnualLeaveTimes("");
			} else {
				attendance.setAnnualLeaveTimes(tbSchedules.getAnnualLeaveTimes().toString());
			}
			if (tbSchedules.getFormNo() == null) {
				attendance.setFormNo("");
			} else {
				attendance.setFormNo(tbSchedules.getFormNo());
			}
			attendanceInfoList.add(attendance);
			countInfo++;
		}
		logger.info("Attendance Info Count:" + countInfo);
		return attendanceInfoList;
	}
	@Override
	public int transAttInfoToObjAndInsert(InsertAttendanceInfo insertAttendanceInfo, String account) throws Exception {
		// get input data, and create necessary data to insert
		// start
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// create necessary data to insert
		String createdAtS = sdf.format(new Date());
		Date createdAt = sdf.parse(createdAtS);
		// get input data
		Date endTime = sdf.parse(insertAttendanceInfo.getEndTime());
		Date startTime = sdf.parse(insertAttendanceInfo.getStartTime());
		String note = insertAttendanceInfo.getNote();
		int type = Integer.parseInt(insertAttendanceInfo.getType());
		String annualLeaveTimes = insertAttendanceInfo.getAnnualLeaveTimes();
		Float fAnnualLeaveTimes = Float.valueOf(annualLeaveTimes);
		// end
		// set data in model and insert
		TbSchedules tbSchedules = new TbSchedules();
		tbSchedules.setStartTime(startTime);
		tbSchedules.setEndTime(endTime);
		tbSchedules.setNote(note);
		tbSchedules.setType(type);
		tbSchedules.setTbEmployeesByUser(new TbEmployees());
		tbSchedules.getTbEmployeesByUser().setUsername(account);
		tbSchedules.setStatus("CREATED");
		tbSchedules.setCreatedAt(createdAt);
		tbSchedules.setAnnualLeaveTimes(fAnnualLeaveTimes);
		if (insertAttendanceInfo.getDeputy() != null) {
			tbSchedules.setTbEmployeesByDeputy(new TbEmployees());
			tbSchedules.getTbEmployeesByDeputy().setUsername(insertAttendanceInfo.getDeputy());
		} else {
			tbSchedules.setTbEmployeesByDeputy(null);
		}
		schedulesDao.save(tbSchedules);
		return tbSchedules.getId();
	}
	@Override
	public void sendEmail(int id) throws Exception {
		Config config = Config.getInstance();
		if (config.getValue("mail_attendanceapply_send").equals("Y")) {
			TbSchedules record = schedulesDao.findById(id);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String account = record.getTbEmployeesByUser().getUsername();
			String start = sdf.format(record.getStartTime());
			String end = sdf.format(record.getEndTime());
			String hour = "0";
			if (record.getAnnualLeaveTimes() != 0) {
				hour = Float.toString(record.getAnnualLeaveTimes());
			}
			String note = record.getNote();
			Integer scheduleType = record.getType();
			String type = "";
			if (scheduleType == 2) {
				type = "出差";
			}
			if (scheduleType == 3) {
				type = "特休";
			}
			if (scheduleType == 4) {
				type = "事假";
			}
			if (scheduleType == 5) {
				type = "病假";
			}
			if (scheduleType == 6) {
				type = "公假";
			}
			if (scheduleType == 7) {
				type = "婚假";
			}
			if (scheduleType == 8) {
				type = "喪假";
			}
			if (scheduleType == 9) {
				type = "加班";
			}
			if (scheduleType == 10) {
				type = "補休";
			}
			if (scheduleType == 11) {
				type = "外出";
			}

			String recipient, subject, base, text;
			StringBuilder builder = new StringBuilder();
			builder.append("員工 ");
			builder.append(account);
			builder.append(" 已提出自 ");
			builder.append(start);
			builder.append(" 至 ");
			builder.append(end);
			if (!hour.equals("0")) {
				builder.append(" ，共 ");
				builder.append(hour);
				builder.append(" 小時");
			}
			builder.append("之 ");
			builder.append(type);
			builder.append(" 申請");
			if (record.getTbEmployeesByDeputy() != null) {
				builder.append("\n\n代理人: ");
				builder.append(record.getTbEmployeesByDeputy().getUsername());
			}
			builder.append(" \n\n備註 : \n\n");
			builder.append(note);
			builder.append("\n");
			if (scheduleType != 2 && scheduleType != 9 && scheduleType != 11) {
				Map<String, Object> time = new HashMap<>();
				List<TbSchedules> schedules = schedulesDao.findSchedulesOfYear(account, record.getStartTime());
				Iterator<TbSchedules> it = schedules.iterator();
				float annual = 0, personal = 0, sick = 0, statutory = 0, marital = 0, funeral = 0;
				float hours;
				while (it.hasNext()) {
					TbSchedules schedulesModel = it.next();
					if (schedulesModel.getAnnualLeaveTimes() == null) {
						hours = 0;
					} else {
						hours = schedulesModel.getAnnualLeaveTimes();
					}
					switch (schedulesModel.getType()) {
					case 3:
						annual += hours;
						break;
					case 4:
						personal += hours;
						break;
					case 5:
						sick += hours;
						break;
					case 6:
						statutory += hours;
						break;
					case 7:
						marital += hours;
						break;
					case 8:
						funeral += hours;
						break;
					default:
						break;
					}
				}
				List<TbAnnualLeave> annualLeave = annualLeaveDao.findByNameAndYear(account,
						String.valueOf(record.getStartTime().getYear() + 1900));
				Iterator<TbAnnualLeave> it1 = annualLeave.iterator();
				String annualLeavehour = "0";
				if (it1.hasNext()) {
					TbAnnualLeave AnnualLeave = it1.next();
					annualLeavehour = AnnualLeave.getEntitledHours();
				}
				time.put("annual", annual);
				time.put("personal", personal);
				time.put("sick", sick);
				time.put("statutory", statutory);
				time.put("marital", marital);
				time.put("funeral", funeral);
				time.put("hour", annualLeavehour);
				builder.append("\n\n");
				builder.append(account);
				builder.append("本年度可用特休總時數為 : ");
				builder.append(time.get("hour"));
				builder.append("\n\n本年度已簽核之差勤時數 : \n\n");
				builder.append("特休 : ");
				builder.append(time.get("annual"));
				builder.append("\t\t事假 : ");
				builder.append(time.get("personal"));
				builder.append("\t\t病假 : ");
				builder.append(time.get("sick"));
			}
			base = builder.toString();

			subject = account + " " + type + "申請";

			String signer;

			List<TbEmployees> employee = employeesDao.findByUserName(account);
			List<TbEmployees> list = accountService.getApplyMailRecipient(employee.get(0).getDepartmentId(), account);

			String signCode, rejectCode;
			boolean deputySend = false;
			for (int i = 0; i < list.size(); i++) {
				signer = list.get(i).getUsername();
				if (record.getTbEmployeesByDeputy() != null) {
					if (signer.equals(record.getTbEmployeesByDeputy().getUsername())) {
						deputySend = true;
					}
				}
				signCode = eccrypt(id + signer + "sign");
				TbSignCode signObject = new TbSignCode();
				signObject.setHashcode(signCode);
				signObject.setSigner(signer);
				signObject.setScheduleId(id);
				signCodeDao.save(signObject);

				rejectCode = eccrypt(id + signer + "reject");
				TbRejectCode rejectObject = new TbRejectCode();
				rejectObject.setHashcode(rejectCode);
				rejectObject.setSigner(signer);
				rejectObject.setScheduleId(id);
				rejectedCodeDao.save(rejectObject);

				recipient = signer + "@yesee.com.tw";

				text = base;

				text += "\n\n\n點擊連結以直接簽核 : " + config.getValue("mail_smtpsetting_signURL") + signCode;
				text += "\n\n點擊連結以直接駁回 : " + config.getValue("mail_smtpsetting_rejectURL") + rejectCode;

				sendMailService.sendEmail(recipient, subject, text);
			}
			if (record.getTbEmployeesByDeputy() != null && !deputySend) {
				sendMailService.sendEmail(record.getTbEmployeesByDeputy().getUsername() + "@yesee.com.tw", subject,
						base);
			}
		}
	}

	// 加密
	private String eccrypt(String info) throws NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance("SHA");
		byte[] srcBytes = info.getBytes();
		// 使用 srcBytes 更新摘要
		sha.update(srcBytes);
		// 完成哈希計算，得到 result
		byte[] resultBytes = sha.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < resultBytes.length; i++) {
			sb.append(byte2Hex(resultBytes[i]));
		}
		String code = sb.toString().toUpperCase();
		return code;
	}

	// 字節轉換
	private String byte2Hex(byte b) {
		String[] h = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		int i = b;
		if (i < 0) {
			i += 256;
		}
		return h[i / 16] + h[i % 16];
	}

	@Override
	public void transAttInfoToObjAndUpdate(UpdateAttendanceInfo updateAttendanceInfo, String account) throws Exception {

		// get input data, and create necessary data to update
		// start
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// create necessary data
		String updateAtS = sdf.format(new Date());
		Date updateAt = sdf.parse(updateAtS);

		// get input data
		Date endTime = sdf.parse(updateAttendanceInfo.getEndTime());
		Date startTime = sdf.parse(updateAttendanceInfo.getStartTime());

		String note = updateAttendanceInfo.getNote();
		int type = Integer.parseInt(updateAttendanceInfo.getType());
		int id = Integer.parseInt(updateAttendanceInfo.getId());

		String status = updateAttendanceInfo.getStatus();

		String annualLeaveTimes = updateAttendanceInfo.getAnnualLeaveTimes();
		Float fAnnualLeaveTimes = Float.valueOf(annualLeaveTimes);
		// end

		// set data in model and update
		TbSchedules tbSchedules = schedulesDao.findById(id);
		// new TbSchedules();

		tbSchedules.setStartTime(startTime);
		tbSchedules.setEndTime(endTime);
		tbSchedules.setNote(note);
		tbSchedules.setType(type);
		tbSchedules.setUpdateUser(account);
		tbSchedules.setUpdatedAt(updateAt);
		// tbSchedules.setId(id);
		tbSchedules.setStatus(status);
		tbSchedules.setAnnualLeaveTimes(fAnnualLeaveTimes);
		if (updateAttendanceInfo.getDeputy() != null) {
			tbSchedules.setTbEmployeesByDeputy(new TbEmployees());
			tbSchedules.getTbEmployeesByDeputy().setUsername(updateAttendanceInfo.getDeputy());
		} else {
			tbSchedules.setTbEmployeesByDeputy(null);
		}

		schedulesDao.save(tbSchedules);

	}

	@Override
	public void setAttInfoToObjAndDelete(String id) throws Exception {

		// before delete, log it!
		TbSchedules skd = schedulesDao.findById(Integer.parseInt(id));

		String status = skd.getStatus();
		String type = skd.returnTypeString();
		String user = skd.getTbEmployeesByUser().getUsername();
		String note = skd.getNote();
		String signer = null;
		if (skd.getTbEmployeesBySigner() != null) {
			signer = skd.getTbEmployeesBySigner().getUsername();
		}
		String updatedUser = skd.getUpdateUser();
		String startTime = skd.getStartTime().toString();
		String updateTime;
		if (skd.getUpdatedAt() != null) {
			updateTime = skd.getUpdatedAt().toString();
		} else {
			updateTime = "";
		}
		String endTime = skd.getEndTime().toString();
		String createdAt = skd.getCreatedAt().toString();
		logger.info("Delete attendance info:");
		logger.info("status: " + status + " type: " + type + " user: " + user + " note: " + note);
		logger.info("signer: " + signer + " updatedUser " + updatedUser);
		logger.info("startTime: " + startTime + " updateTime: " + updateTime + " endTime: " + endTime + " createdAt: "
				+ createdAt);

		schedulesDao.delete(skd);
	}

	@Override
	public List<TbEmployees> getEmployees(String account, TbEmployees entity) throws Exception {
		Map<Integer, String> departmentMap = accountService.getDepList(account);
		List<TbEmployees> empList = employeesDao.getList(entity);
		List<TbEmployees> subList = empList.stream().filter(e -> {
			for (Integer key : departmentMap.keySet()) {
				if (key.equals(Integer.parseInt(e.getDepartmentId()))) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		return subList;
	}

	@Override
	public String getAnnualLeave(String account, String year) throws Exception {
		String hour = "0";
		// get all annual leave info
		TbAnnualLeaveId annId = new TbAnnualLeaveId();
		annId.setEmpName(account);
		annId.setYear(year);
		List<TbAnnualLeave> list = annualLeaveDao.findById(annId);

		Iterator<TbAnnualLeave> it = list.iterator();
		if (it.hasNext()) {
			TbAnnualLeave record = (TbAnnualLeave) it.next();
			hour = record.getEntitledHours();
		}
		return hour;
	}

	@Override
	public String getAnnualLeaveInfo(String account) throws Exception {

		// get all annual leave info
		List<TbSchedules> list = schedulesDao.getAnnualLeaveInfo(account);

		Iterator<TbSchedules> it = list.iterator();
		Float allTInterval = (float) 0;
		while (it.hasNext()) {
			TbSchedules tbSchedules = it.next();

			// check annualLeave current year or not
			// if current year, calculate it
			Date startDate = tbSchedules.getStartTime();
			Date endDate = tbSchedules.getEndTime();
			Calendar startC = DateUtil.toCalendar(startDate);
			Calendar endC = DateUtil.toCalendar(endDate);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			if (startC.get(Calendar.YEAR) == currentYear && endC.get(Calendar.YEAR) == currentYear) {

				if (tbSchedules.getAnnualLeaveTimes() == null) {
					allTInterval += (float) 0;
				} else {
					allTInterval += tbSchedules.getAnnualLeaveTimes();
				}

			}

		}

		logger.info("user:" + account);
		logger.info(" annual leave taken " + allTInterval);

		return allTInterval.toString();
	}

	@Override
	public int check(String id) throws Exception {
		return schedulesDao.checkSignedOrRejected(Integer.parseInt(id));
	}

	@Override
	public List<Map<String, Object>> getUnsignedAttRecords(String nameSelect, String depId, String authorise,
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
		List<TbSchedules> list = new ArrayList<TbSchedules>();
		if (!nameList.isEmpty()) {
			list = schedulesDao.findAllScheduleByStatus(nameList, "CREATED");
		}
		Iterator<TbSchedules> it = list.iterator();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> result = new ArrayList<>();
		String time;
		while (it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			TbSchedules record = it.next();
			if (record.getAnnualLeaveTimes() == null || record.getAnnualLeaveTimes() == 0) {
				time = "";
			} else {
				time = record.getAnnualLeaveTimes().toString();
			}
			map.put("id", record.getId());

			if ("TW".equals(nameSelect)) {
				map.put("user", record.getTbEmployeesByUser().getChineseName());
				if (record.getTbEmployeesByDeputy() != null) {
					map.put("deputy", record.getTbEmployeesByDeputy().getChineseName());
				} else {
					map.put("deputy", "");
				}
			} else {
				map.put("user", record.getTbEmployeesByUser().getUsername());
				if (record.getTbEmployeesByDeputy() != null) {
					map.put("deputy", record.getTbEmployeesByDeputy().getUsername());
				} else {
					map.put("deputy", "");
				}
			}

			map.put("department", depForEmp.get(record.getTbEmployeesByUser().getUsername()));
			map.put("type", record.getType());
			map.put("startTime", df.format(record.getStartTime()));
			map.put("endTime", df.format(record.getEndTime()));
			map.put("createdAt", jd.format(record.getCreatedAt()));
			if (record.getUpdatedAt() != null) {
				map.put("updatedAt", sdf.format(record.getUpdatedAt()));
			} else {
				map.put("updatedAt", "null");
			}
			map.put("time", time);
			map.put("note", record.getNote());
			result.add(map);
		}
		return result;
	}

	@Override
	public int checkScheduleAndUpdate(String account, String id, String updatedAt, String status, String reason)
			throws Exception {
		int check = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TbSchedules record = schedulesDao.findById(Integer.parseInt(id));
		boolean updateCheck = false;
		if ("null".equals(updatedAt)) {
			if (record.getUpdatedAt() == null) {
				updateCheck = true;
			}
		} else if (record.getUpdatedAt().compareTo(sdf.parse(updatedAt)) == 0) {
			updateCheck = true;
		}
		if (record == null || record.getTbEmployeesBySigner() != null || !updateCheck
				|| !record.getStatus().equals("CREATED")) {
			check = 0;
		} else {
			updateScheduleStatus(account, record, status, reason);
		}
		return check;
	}

	@Override
	public void updateScheduleStatus(String account, TbSchedules record, String status, String reason)
			throws Exception {
		record.setTbEmployeesBySigner(new TbEmployees());
		record.getTbEmployeesBySigner().setUsername(account);
		record.setSignedAt(new Date());
		record.setStatus(status);
		if (status.equals("REJECTED")) {
			record.setNote(record.getNote() + " ,駁回原因：" + reason);
		} else if (record.getType() == 2) {
			record.setFormNo(getSerialNumber(record.getCreatedAt()));
		}
		schedulesDao.save(record);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					signService.sendRespondEmail(record);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		thread.start();
	}

	@Override
	public List<Map<String, Object>> getAttRecords(String nameSelect, String status, String depId, String authorise,
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
		List<TbSchedules> list = new ArrayList<TbSchedules>();
		if (!nameList.isEmpty()) {
			list = schedulesDao.findScheduleByStatus(nameList, status, startDate, endDate);
		}
		Iterator<TbSchedules> it = list.iterator();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> result = new ArrayList<>();
		String time;
		while (it.hasNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			TbSchedules record = it.next();
			if (record.getAnnualLeaveTimes() == null || record.getAnnualLeaveTimes() == 0) {
				time = "";
			} else {
				time = record.getAnnualLeaveTimes().toString();
			}
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
				if (record.getTbEmployeesByDeputy() != null) {
					map.put("deputy", record.getTbEmployeesByDeputy().getChineseName());
				} else {
					map.put("deputy", "");
				}
			} else {
				if (record.getTbEmployeesByUser() != null) {
					map.put("user", record.getTbEmployeesByUser().getUsername());
				}
				if (record.getTbEmployeesBySigner() != null) {
					map.put("signer", record.getTbEmployeesBySigner().getUsername());
				}
				if (record.getTbEmployeesByDeputy() != null) {
					map.put("deputy", record.getTbEmployeesByDeputy().getUsername());
				} else {
					map.put("deputy", "");
				}
			}

			map.put("department", depForEmp.get(record.getTbEmployeesByUser().getUsername()));
			map.put("type", record.getType());
			map.put("startTime", df.format(record.getStartTime()));
			map.put("endTime", df.format(record.getEndTime()));
			map.put("createdAt", jd.format(record.getCreatedAt()));
			map.put("time", time);
			map.put("signTime", jd.format(record.getSignedAt()));
			map.put("reason", reason);
			map.put("note", note);
			if (record.getFormNo() != null) {
				map.put("formNo", record.getFormNo());
			} else {
				map.put("formNo", "");
			}
			result.add(map);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getYearsAttRecords(String nameSelect, String depId, String authorise,
			String account, int year) throws Exception {
		List<Map<String, Object>> result = new ArrayList<>();
		int next = year + 1;
		String startDate = year + "-01-01";
		String endDate = next + "-01-01";
		List<TbAnnualLeave> annualList = annualLeaveDao.getListByYear(year);
		Map<String, Object> annualMap = new HashMap<>();
		for (int i = 0; i < annualList.size(); i++) {
			annualMap.put(annualList.get(i).getId().getEmpName(), annualList.get(i).getEntitledHours());
		}
		List<TbDepartment> depList = departmentDao.getList();
		Map<String, Object> departmentMap = new HashMap<>();
		for (int i = 0; i < depList.size(); i++) {
			departmentMap.put(String.valueOf(depList.get(i).getId()), depList.get(i).getName());
		}
		List<TbEmployees> employeesList;
		if (authorise.equals("4")) {
			employeesList = employeesDao.findByUserName(account);
		} else {
			employeesList = accountService.getSubordinateList(authorise, depId, null);
		}
		List<String> statusList = new ArrayList<>();
		statusList.add("CREATED");
		statusList.add("SIGNED");
		for (int i = 0; i < employeesList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<TbSchedules> list = schedulesDao.findAllScheduleByStatuses(employeesList.get(i).getUsername(),
					statusList, startDate, endDate);
			Iterator<TbSchedules> it = list.iterator();
			float annual = 0, personal = 0, sick = 0, statutory = 0, marital = 0, funeral = 0;
			float annualC = 0, personalC = 0, sickC = 0, statutoryC = 0, maritalC = 0, funeralC = 0;
			String hour = "0";
			float time;
			while (it.hasNext()) {
				TbSchedules record = it.next();
				if (record.getAnnualLeaveTimes() == null) {
					time = 0;
				} else {
					time = record.getAnnualLeaveTimes();
				}
				switch (record.getType()) {
				case 3:
					if (record.getStatus().equals("SIGNED")) {
						annual += time;
					} else {
						annualC += time;
					}
					break;
				case 4:
					if (record.getStatus().equals("SIGNED")) {
						personal += time;
					} else {
						personalC += time;
					}
					break;
				case 5:
					if (record.getStatus().equals("SIGNED")) {
						sick += time;
					} else {
						sickC += time;
					}
					break;
				case 6:
					if (record.getStatus().equals("SIGNED")) {
						statutory += time;
					} else {
						statutoryC += time;
					}
					break;
				case 7:
					if (record.getStatus().equals("SIGNED")) {
						marital += time;
					} else {
						maritalC += time;
					}
					break;
				case 8:
					if (record.getStatus().equals("SIGNED")) {
						funeral += time;
					} else {
						funeralC += time;
					}
					break;
				}
			}
			if (annualMap.get(employeesList.get(i).getUsername()) != null) {
				hour = (String) annualMap.get(employeesList.get(i).getUsername());
			}
			map.put("accountName", employeesList.get(i).getUsername());

			if ("TW".equals(nameSelect)) {
				map.put("name", employeesList.get(i).getChineseName());
			} else {
				map.put("name", employeesList.get(i).getUsername());
			}

			map.put("dep", departmentMap.get(employeesList.get(i).getDepartmentId()));
			map.put("annual", annual);
			map.put("personal", personal);
			map.put("sick", sick);
			map.put("statutory", statutory);
			map.put("marital", marital);
			map.put("funeral", funeral);
			map.put("annualC", annualC);
			map.put("personalC", personalC);
			map.put("sickC", sickC);
			map.put("statutoryC", statutoryC);
			map.put("maritalC", maritalC);
			map.put("funeralC", funeralC);
			map.put("hour", hour);
			result.add(map);
		}
		return result;
	}

	@Override
	public List<TbSchedules> getYearDetail(String name, String type, int year) throws Exception {
		int next = year + 1;
		String startDate = year + "-01-01";
		String endDate = next + "-01-01";
		List<String> statusList = new ArrayList<>();
		statusList.add("CREATED");
		statusList.add("SIGNED");
		List<TbSchedules> list = schedulesDao.findAllScheduleByType(name, type, statusList, startDate, endDate);
		return list;
	}

	@Override
	public String getSerialNumber(Date createdAt) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<TbSchedules> list = schedulesDao.findTravelByCreatedAt(createdAt);
		int last;
		String id, number = sdf.format(createdAt);
		if (list.size() != 0) {
			id = list.get(0).getFormNo();
			id = id.substring(id.length() - 6);
			last = Integer.parseInt(id) + 1;
			for (int i = 6; i > String.valueOf(last).length(); i--) {
				number += "0";
			}
			number += last;
		} else {
			number += "000001";
		}
		return number;
	}
	
	@Override
	public List<VTbSchedules> getAvailableTimeList(String user) throws Exception {
		
		 List<VTbSchedules> availableTimeList = schedulesDao.findSchedulesOfAvailableTime(user);
		 logger.info(user+" AvailableTime : "+availableTimeList.get(0).getAvailableTime());
		 if(availableTimeList.isEmpty()) {
			 return null;
		 }
		 
		 return availableTimeList;
		
	}

}
