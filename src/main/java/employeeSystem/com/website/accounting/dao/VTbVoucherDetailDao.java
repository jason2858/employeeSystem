package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

import employeeSystem.com.website.accounting.model.VTbVoucherDetail;

public interface VTbVoucherDetailDao {

	public List<VTbVoucherDetail> getVoucherDetailVList(String voucherNo, String company, String applicant,
			String status, String customer, String cusTaxId, String item, String projectId, String pageNo,
			String pageSize) throws Exception;

	public Map<String, Integer> getDetailTotal(String start, String end, String iId);

}
