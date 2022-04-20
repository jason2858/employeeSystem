package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbVoucherSign;

public interface VoucherSignDao {

	public List<TbVoucherSign> getList(Map<String, Object> param, List<String> orderBy, Boolean desc) throws Exception;

	/**
	 * @param voucherNo
	 * @return
	 * @throws Exception 取出TbVoucherSign內符合voucherNo的資料 若無則回傳null
	 */
	public List<TbVoucherSign> getVoucherSignListByVoucherNo(String voucherNo) throws Exception;

	public void save(TbVoucherSign tbVoucherSign) throws Exception;

	public void update(TbVoucherSign tbVoucherSign) throws Exception;

	public void delete(String voucherNo) throws Exception;
}
