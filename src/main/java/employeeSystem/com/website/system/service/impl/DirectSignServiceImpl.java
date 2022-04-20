package com.yesee.gov.website.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.RejectedCodeDao;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.dao.SignCodeDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.TbSignCode;
import com.yesee.gov.website.service.AttendanceService;
import com.yesee.gov.website.service.DirectSignService;
import com.yesee.gov.website.service.SignService;

@Service("directSignService")
public class DirectSignServiceImpl implements DirectSignService {
	private static final Logger logger = LogManager.getLogger(DirectSignServiceImpl.class);

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private SignCodeDao signCodeDao;

	@Autowired
	private RejectedCodeDao rejectedCodeDao;

	@Autowired
	private SignService signService;

	@Autowired
	private AttendanceService attendanceService;

	@Override
	public Map<String, Object> getDirectSignMap(String code) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		TbSignCode signCode = signCodeDao.findByCode(code);
		String result = "連結已失效，請使用最新的連結或登入內部系統操作";
		if (signCode != null) {
			Integer id = signCode.getScheduleId();
			TbSchedules model = schedulesDao.getById(id);
			map.put("send", false);
			if (model != null) {
				if (model.getStatus().equals("SIGNED")) {
					result = "該申請已被簽核";
				} else if (model.getStatus().equals("REJECTED")) {
					result = "該申請已被駁回";
				} else {
					result = "簽核成功";
					model.setStatus("SIGNED");
					model.setTbEmployeesBySigner(new TbEmployees());
					model.getTbEmployeesBySigner().setUsername(signCode.getSigner());
					model.setSignedAt(new Date());
					if (model.getType() == 2) {
						model.setFormNo(attendanceService.getSerialNumber(model.getCreatedAt()));
					}
					schedulesDao.save(model);
					if (model.getOriginId() != null) {
						TbSchedules origin = schedulesDao.findById(model.getOriginId());
						origin.setStatus("CANCELLED");
						origin.setUpdatedAt(new Date());
						schedulesDao.save(origin);
					}
					signCodeDao.deleteBySchdulesId(id);
					rejectedCodeDao.deleteBySchdulesId(id);
					map.put("id", id);
					map.put("send", true);
					map.put("result", result);
					Thread thread = new Thread(new Runnable() {
						public void run() {
							try {
								signService.sendRespondEmail(model);
							} catch (Exception e) {
								logger.error("error : ", e);
							}
						}
					});
					thread.start();
					// List<TbSignCode> models = signCodeDao.findByCode(code);
					// Integer id = models.stream().map(TbSignCode::getScheduleId).findAny().get();
					// TbSchedules model = schedulesDao.getById(id);
					//
					// String result = "查無此筆資料或已被刪除";
					// map.put("send", false);
					// if (model != null) {
					// if (model.getStatus().equals("SIGNED")) {
					// result = "該申請已被簽核";
					// } else if (model.getStatus().equals("REJECTED")) {
					// result = "該申請已被駁回";
					// } else if (model.getUpdateUser() != null || model.getUpdatedAt() != null) {
					// result = "該申請已被修改，請至簽核頁面查看";
					// } else {
					// result = "簽核成功";
					// model.setStatus("SIGNED");
					// model.setTbEmployeesBySigner(new TbEmployees());
					// model.getTbEmployeesBySigner().setUsername(models.get(0).getSigner());
					// model.setSignedAt(new Date());
					// if(model.getType() == 2) {
					// model.setFormNo(attendanceService.getSerialNumber(model.getCreatedAt()));
				}
				// schedulesDao.save(model);
				// map.put("id", id);
				// map.put("send", true);
				// map.put("result", result);
				//
				// TbSchedules record = schedulesDao.findById(id);
				// Thread thread = new Thread(new Runnable() {
				// public void run() {
				// try {
				// signService.sendRespondEmail(record);
				// } catch (Exception e) {
				// logger.error(e);
				// }
				// }
				// });
				// thread.start();
			}
		}
		map.put("result", result);
		logger.info(result);
		return map;
	}
}