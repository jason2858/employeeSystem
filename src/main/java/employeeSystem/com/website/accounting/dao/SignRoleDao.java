package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.TbSignRole;

public interface SignRoleDao {
	public List<TbSignRole> getList(Map<String, Object> param) throws Exception;
}
