package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.VTbVoucherHedge;

public interface VTbVoucherHedgeDao {
	public List<VTbVoucherHedge> getList(Map<String, Object> param) throws Exception;

}
