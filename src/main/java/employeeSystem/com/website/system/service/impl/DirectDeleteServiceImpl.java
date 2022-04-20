package com.yesee.gov.website.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DeleteCodeDao;
import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.model.TbDeleteCode;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.service.DirectDeleteService;
import com.yesee.gov.website.service.SignService;

@Service("directDeleteService")
public class DirectDeleteServiceImpl implements DirectDeleteService {
	private static final Logger logger = LogManager.getLogger(DirectDeleteServiceImpl.class);

	@Autowired
	private SchedulesDao schedulesDao;

	@Autowired
	private DeleteCodeDao deleteCodeDao;

	@Autowired
	private SignService signService;

	@Override
	public Map<String, Object> getDirectDeleteMap(String code) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		TbDeleteCode deleteCode = deleteCodeDao.findByCode(code);
		String result = "連結已失效，請使用最新的連結或登入內部系統操作";
		if (deleteCode != null) {
			Integer id = deleteCode.getScheduleId();
			TbSchedules model = schedulesDao.getById(id);
			map.put("send", false);
			if (model != null) {
				if ("DELETED".equals(model.getStatus())) {
					result = "該申請已被刪除";
				} else if (!"WAIT_DELETE".equals(model.getStatus())) {
					result = "該申請已被取消";
				} else {
					result = "刪除成功";
					model.setStatus("DELETED");
					model.setTbEmployeesBySigner(new TbEmployees());
					model.getTbEmployeesBySigner().setUsername(deleteCode.getSigner());
					model.setSignedAt(new Date());
					schedulesDao.save(model);
					deleteCodeDao.deleteBySchdulesId(id);
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
				}
			}
		}
		map.put("result", result);
		logger.info(result);
		return map;
	}
}