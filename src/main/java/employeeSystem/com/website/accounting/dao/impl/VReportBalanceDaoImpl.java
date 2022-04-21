package employeeSystem.com.website.accounting.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.VReportBalanceDao;
import employeeSystem.com.website.accounting.model.VTbReportBalance;

@Repository("vReportBalanceDao")
public class VReportBalanceDaoImpl extends BaseDao<VTbReportBalance> implements VReportBalanceDao {

	@Override
	public List<VTbReportBalance> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(VTbReportBalance.class, param, null, null, false, 0, 0);
	}

}
