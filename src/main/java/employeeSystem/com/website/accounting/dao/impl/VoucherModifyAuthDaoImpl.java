package employeeSystem.com.website.accounting.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.VoucherModifyAuthDao;
import employeeSystem.com.website.accounting.model.TbVoucherModifyAuth;

@Repository("VoucherModifyAuthDao")
public class VoucherModifyAuthDaoImpl extends BaseDao<TbVoucherModifyAuth> implements VoucherModifyAuthDao {

	@Override
	public List<TbVoucherModifyAuth> findVoucherModifyAuthByVNo(String voucherNo, String account) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();

		param.put("entity.voucherNo.voucherNo", voucherNo);
		param.put("entity.createUser", account);

		List<String> order = new ArrayList<>();
		order.add("entity.createDate");

		return super.findByHql(TbVoucherModifyAuth.class, param, null, order, true, 0, 0);
	}

	@Override
	public List<TbVoucherModifyAuth> findVoucherModifyAuthByVNoAndToken(String voucherNo, String token)
			throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();

		param.put("entity.voucherNo.voucherNo", voucherNo);
		param.put("entity.token", token);

		return super.findByHql(TbVoucherModifyAuth.class, param, null, null, false, 0, 0);
	}

}
