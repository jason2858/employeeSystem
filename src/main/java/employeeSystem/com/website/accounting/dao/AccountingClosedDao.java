package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.TbAccountingClosed;
import employeeSystem.com.website.accounting.model.TbAccountingClosedPK;

public interface AccountingClosedDao {

	public List<TbAccountingClosed> getList(Map<String, Object> param) throws Exception;

	public TbAccountingClosed findById(TbAccountingClosedPK tbAccountingClosedPK) throws Exception;

	public void save(TbAccountingClosed accountingClosed) throws Exception;

	public void update(TbAccountingClosed accountingClosed) throws Exception;

	public List<TbAccountingClosed> getLastClosedMonth() throws Exception;
}
