package employeeSystem.com.website.accounting.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.SignCommonDao;
import employeeSystem.com.website.accounting.model.TbSignCommon;

@Repository("signCommonDao")
public class SignCommonDaoImpl extends BaseDao<TbSignCommon> implements SignCommonDao {

	@Override
	public List<TbSignCommon> getSignCommonList() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		return super.findByHql(TbSignCommon.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbSignCommon> getSignCommon(String signName) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.signName", signName);
		return super.findByHql(TbSignCommon.class, param, null, null, false, 0, 0);
	}
}
