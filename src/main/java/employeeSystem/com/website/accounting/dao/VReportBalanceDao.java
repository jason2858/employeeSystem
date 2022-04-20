package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.VTbReportBalance;

public interface VReportBalanceDao {

	public List<VTbReportBalance> getList(Map<String, Object> param) throws Exception;
}
