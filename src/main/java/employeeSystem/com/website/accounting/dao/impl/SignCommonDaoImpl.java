package com.yesee.gov.website.dao.accounting.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.SignCommonDao;
import com.yesee.gov.website.model.accounting.TbSignCommon;

@Repository("signCommonDao")
public class SignCommonDaoImpl extends BaseDao<TbSignCommon> implements SignCommonDao {

	@Override
	public List<TbSignCommon> getSignCommonList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		return super.findByHql(TbSignCommon.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbSignCommon> getSignCommon(String signName) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.signName", signName);
		return super.findByHql(TbSignCommon.class, param, null, null, false, 0, 0);
	}
}
