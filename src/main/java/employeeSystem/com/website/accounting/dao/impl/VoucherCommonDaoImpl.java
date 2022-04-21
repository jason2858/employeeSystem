package employeeSystem.com.website.accounting.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.VoucherCommonDao;
import employeeSystem.com.website.accounting.model.TbVoucherCommon;

@Repository("voucherCommonDao")
public class VoucherCommonDaoImpl extends BaseDao<TbVoucherCommon> implements VoucherCommonDao {

	@Override
	public List<TbVoucherCommon> getVoucherCommonList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		return super.findByHql(TbVoucherCommon.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherCommon> findVoucherCommonListByVoucherName(String voucherName) throws Exception {
		;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherName", voucherName);
		return super.findByHql(TbVoucherCommon.class, param, null, null, false, 0, 0);
	}

}
