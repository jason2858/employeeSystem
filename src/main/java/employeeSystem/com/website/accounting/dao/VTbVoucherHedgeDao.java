package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.VTbVoucherHedge;

public interface VTbVoucherHedgeDao {
	public List<VTbVoucherHedge> getList(Map<String, Object> param) throws Exception;

}
