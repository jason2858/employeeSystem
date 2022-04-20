package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbHedge;

public interface HedgeDao {

	public List<TbHedge> getList(Map<String, Object> param) throws Exception;

	public TbHedge findById(String id) throws Exception;

	public void save(TbHedge tbHedge) throws Exception;

	public void update(TbHedge tbHedge) throws Exception;
	
	public void delete(TbHedge tbHedge) throws Exception;

	public List<TbHedge> findHedgeNoByVoucherNo(String voucherNo) throws Exception;
}
