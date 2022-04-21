package employeeSystem.com.website.accounting.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.AccountingClassDao;
import employeeSystem.com.website.accounting.model.TbAccountingClass;

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
