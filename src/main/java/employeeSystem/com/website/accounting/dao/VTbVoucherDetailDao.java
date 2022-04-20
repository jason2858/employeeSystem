package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.VTbVoucherDetail;

public interface VTbVoucherDetailDao {

	public List<VTbVoucherDetail> getVoucherDetailVList(String voucherNo, String company, String applicant, String status,
			String customer, String cusTaxId, String item, String projectId, String pageNo, String pageSize)
			throws Exception;

	public Map<String, Integer> getDetailTotal(String start, String end, String iId);

}
