package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbSignRole;

public interface SignRoleDao {
	public List<TbSignRole> getList(Map<String, Object> param) throws Exception;
}
