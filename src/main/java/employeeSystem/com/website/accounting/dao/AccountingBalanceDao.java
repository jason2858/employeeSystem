package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbAccountingBalance;

public interface AccountingBalanceDao {
	public void save(TbAccountingBalance tbAccountingBalance) throws Exception;

	public List<TbAccountingBalance> getList(Map<String, Object> param) throws Exception;

	public List<TbAccountingBalance> getBList(String month, String year) throws Exception;

	public List<TbAccountingBalance> getBOne(String month, String year, String item) throws Exception;

}
