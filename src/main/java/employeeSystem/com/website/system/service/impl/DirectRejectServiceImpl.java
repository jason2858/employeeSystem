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
import com.yesee.gov.website.model.TbRejectCode;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.service.DirectRejectService;
import com.yesee.gov.website.service.SignService;

@Service("directRejectService")
public class DirectRejectServiceImpl implements DirectRejectService {
	private static final Logger logger = LogManager.getLogger(DirectRejectServiceImpl.class);

	@Autowired
	private SignCodeDao signCodeDao;

	@Autowired
	private RejectedCodeDao rejectedCodeDao;
	// private RejectedCodeDao rejecCodeDao;

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private SignService signService;

	@Override
	public Map<String, Object> getDirectRejectMap(String code, String reason) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		TbRejectCode rejectCode = rejectedCodeDao.findByCode(code);
		String result = "連結已失效，請使用最新的連結或登入內部系統操作";
		if (rejectCode != null) {
			Integer id = rejectCode.getScheduleId();
			TbSchedules model = schedulesDao.getById(id);
			StringBuilder str = new StringBuilder(model.getNote() == null ? "" : model.getNote());
			str.append(" ,駁回原因：");
			str.append(reason);
			// List<TbRejectCode> models = rejecCodeDao.findByCode(code);
			// Integer id =
			// models.stream().map(TbRejectCode::getScheduleId).findAny().get();
			// TbSchedules model = schedulesDao.getById(id);
			map.put("send", false);
			if (model != null) {
				if (model.getStatus().equals("SIGNED")) {
					result = "該申請已被簽核";
				} else if (model.getStatus().equals("REJECTED")) {
					result = "該申請已被駁回";
				} else {
					result = "駁回成功";
					model.setStatus("REJECTED");
					model.setTbEmployeesBySigner(new TbEmployees());
					model.getTbEmployeesBySigner().setUsername(rejectCode.getSigner());
					model.setSignedAt(new Date());
					model.setNote(str.toString());
					schedulesDao.save(model);
					if (model.getOriginId() != null) {
						TbSchedules origin = schedulesDao.findById(model.getOriginId());
						origin.setStatus("SIGNED");
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
							// result = "駁回成功";
							// model.setStatus("REJECTED");
							// model.setTbEmployeesBySigner(new TbEmployees());
							// model.getTbEmployeesBySigner().setUsername(models.get(0).getSigner());
							// model.setSignedAt(new Date());
							// model.setNote(str.toString());
							//
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
						}
					});
					thread.start();
				}
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