package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.VTbReportHedge;

public interface VReportHedgeDao {

	public List<VTbReportHedge> getList(Map<String, Object> param) throws Exception;

	public Map<String, Integer> getBalance(String start, String end, String iId);

}
