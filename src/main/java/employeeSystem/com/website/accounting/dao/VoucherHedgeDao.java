package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.TbVoucherHedge;

public interface VoucherHedgeDao {

	public List<TbVoucherHedge> getList(Map<String, Object> param) throws Exception;

	public List<TbVoucherHedge> findByHedgeNo(Map<String, Object> param) throws Exception;

	public void save(TbVoucherHedge voucherHedge) throws Exception;

	public void update(TbVoucherHedge voucherHedge) throws Exception;

	public List<TbVoucherHedge> findToday() throws Exception;
}
