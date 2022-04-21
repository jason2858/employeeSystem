package employeeSystem.com.website.accounting.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.AccountingClosedDao;
import employeeSystem.com.website.accounting.model.TbAccountingClosed;
import employeeSystem.com.website.accounting.model.TbAccountingClosedPK;

@Repository("accountingClosedDao")
public class AccountingClosedDaoImpl extends BaseDao<TbAccountingClosed> implements AccountingClosedDao {

	@Override
	public List<TbAccountingClosed> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbAccountingClosed.class, param, null, null, false, 0, 0);
	}

	@Override
	public TbAccountingClosed findById(TbAccountingClosedPK tbAccountingClosedPK) throws Exception {
		return super.findById(TbAccountingClosed.class, tbAccountingClosedPK);
	}

	@Override
	public List<TbAccountingClosed> getLastClosedMonth() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("entity.status", "L");

		List<String> order = new ArrayList<String>();
		order.add("concat(year,LPAD(month, 2, 0))");
		return super.findByHql(TbAccountingClosed.class, param, null, order, true, 0, 0);
	}

}
