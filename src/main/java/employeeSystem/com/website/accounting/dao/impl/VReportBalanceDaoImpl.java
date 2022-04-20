package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.VReportBalanceDao;
import com.yesee.gov.website.model.accounting.VTbReportBalance;

@Repository("vReportBalanceDao")
public class VReportBalanceDaoImpl extends BaseDao<VTbReportBalance> implements VReportBalanceDao {

	@Override
	public List<VTbReportBalance> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(VTbReportBalance.class, param, null, null, false, 0, 0);
	}

}
