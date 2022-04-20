package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.AccountingClassDao;
import com.yesee.gov.website.model.accounting.TbAccountingClass;

@Repository("accountingClassDao")
public class AccountingClassDaoImpl extends BaseDao<TbAccountingClass> implements AccountingClassDao {

	@Override
	public List<TbAccountingClass> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbAccountingClass.class, param, null, null, false, 0, 0);
	}

	@Override
	public TbAccountingClass findById(String id) throws Exception {
		return super.findById(TbAccountingClass.class, id);
	}
}
