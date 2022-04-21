package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.TbHedge;

public interface HedgeDao {

	public List<TbHedge> getList(Map<String, Object> param) throws Exception;

	public TbHedge findById(String id) throws Exception;

	public void save(TbHedge tbHedge) throws Exception;

	public void update(TbHedge tbHedge) throws Exception;

	public void delete(TbHedge tbHedge) throws Exception;

	public List<TbHedge> findHedgeNoByVoucherNo(String voucherNo) throws Exception;
}
