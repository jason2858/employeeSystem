package employeeSystem.com.website.accounting.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.SignCommonSetDao;
import employeeSystem.com.website.accounting.model.TbSignCommonSet;

@Repository("signCommonSetDao")
public class SignCommonSetDaoImpl extends BaseDao<TbSignCommonSet> implements SignCommonSetDao {

	@Override
	public List<TbSignCommonSet> getSignCommonSet(String signName) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.signName.signName", signName);
		return super.findByHql(TbSignCommonSet.class, param, null, null, false, 0, 0);
	}

}
