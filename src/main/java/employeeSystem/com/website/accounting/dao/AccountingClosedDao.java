package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbAccountingClosed;
import com.yesee.gov.website.model.accounting.TbAccountingClosedPK;

public interface AccountingClosedDao {

	public List<TbAccountingClosed> getList(Map<String, Object> param) throws Exception;

	public TbAccountingClosed findById(TbAccountingClosedPK tbAccountingClosedPK) throws Exception;

	public void save(TbAccountingClosed accountingClosed) throws Exception;

	public void update(TbAccountingClosed accountingClosed) throws Exception;

	public List<TbAccountingClosed> getLastClosedMonth() throws Exception;
}
