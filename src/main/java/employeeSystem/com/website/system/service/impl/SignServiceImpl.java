package com.yesee.gov.website.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.service.AccountService;
import com.yesee.gov.website.service.SendMailService;
import com.yesee.gov.website.service.SignService;
import com.yesee.gov.website.util.Config;

@Service("signService")
public class SignServiceImpl implements SignService {

	@Autowired
	private EmployeesDao employeesDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SendMailService sendMailService;

	@Override
	public void sendRespondEmail(TbSchedules record) throws Exception {
		Config config = Config.getInstance();
		String sendStatus;
		if (record.getStatus().equals("SIGNED")) {
			sendStatus = "mail_attendancesign_send";
		} else {
			sendStatus = "mail_attendancereject_send";
		}
		if (config.getValue(sendStatus).equals("Y")) {
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
			String signer = null;
			if (record.getTbEmployeesBySigner() != null) {
				signer = record.getTbEmployeesBySigner().getUsername();
			}
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
			List<TbEmployees> employee = employeesDao.findByUserName(account);
			List<TbEmployees> recipientList = accountService.getRespondMailRecipient(employee.get(0).getDepartmentId(),
					account);
			String recipient = "", subject = "", text;
			StringBuilder builder = new StringBuilder();
			builder.append("員工 ");
			builder.append(account);
			builder.append(" 提出自 ");
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
			builder.append(" 申請已被 ");
			builder.append(signer);
			if (record.getStatus().equals("SIGNED")) {
				builder.append(" 簽核\n\n備註 : \n\n");
				subject = account + " " + type + "申請已被簽核";
			} else {
				builder.append(" 駁回\n\n備註 : \n\n");
				subject = account + " " + type + "申請已被駁回";
			}
			builder.append(note);
			if (record.getTbEmployeesByDeputy() != null) {
				builder.append("\n\n代理人: ");
				builder.append(record.getTbEmployeesByDeputy().getUsername());
			}
			text = builder.toString();
			boolean deputySend = false;
			for (int i = 0; i < recipientList.size(); i++) {
				if (record.getTbEmployeesByDeputy() != null) {
					if (recipientList.get(i).getUsername().equals(record.getTbEmployeesByDeputy().getUsername())) {
						deputySend = true;
					}
				}
				recipient = recipientList.get(i).getUsername() + "@yesee.com.tw";
				sendMailService.sendEmail(recipient, subject, text);
			}
			if (record.getTbEmployeesByDeputy() != null && !deputySend) {
				sendMailService.sendEmail(record.getTbEmployeesByDeputy().getUsername() + "@yesee.com.tw", subject,
						text);
			}
		}
	}

}