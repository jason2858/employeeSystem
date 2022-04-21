package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.TbAccountingClass;

public interface AccountingClassDao {

	public List<TbAccountingClass> getList(Map<String, Object> param) throws Exception;

	public TbAccountingClass findById(String id) throws Exception;

	public void save(TbAccountingClass accountingClass) throws Exception;

	public void update(TbAccountingClass accountingClass) throws Exception;

	public void delete(TbAccountingClass accountingClass) throws Exception;
}
