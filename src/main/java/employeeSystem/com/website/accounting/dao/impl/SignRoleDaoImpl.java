package employeeSystem.com.website.accounting.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.SignRoleDao;
import employeeSystem.com.website.accounting.model.TbSignRole;

@Repository("SignRoleDao")
public class SignRoleDaoImpl extends BaseDao<TbSignRole> implements SignRoleDao {

	@Override
	public List<TbSignRole> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbSignRole.class, param, null, null, false, 0, 0);
	}

}
