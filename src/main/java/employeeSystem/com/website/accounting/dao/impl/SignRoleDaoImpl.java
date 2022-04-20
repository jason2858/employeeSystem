package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.SignRoleDao;
import com.yesee.gov.website.model.accounting.TbSignRole;

@Repository("SignRoleDao")
public class SignRoleDaoImpl extends BaseDao<TbSignRole> implements SignRoleDao {

	@Override
	public List<TbSignRole> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbSignRole.class, param, null, null, false, 0, 0);
	}

}
