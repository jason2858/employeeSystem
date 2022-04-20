package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbVoucherDetail;

public interface VoucherDetailDao {

	List<TbVoucherDetail> getList(Map<String, Object> param) throws Exception;

	/**
	 * @param voucherNo
	 * @return
	 * @throws Exception 取出TbVoucherDetail內符合voucherNo的資料 若無則回傳null
	 */
	public List<TbVoucherDetail> findVoucherDetailByVoucherNo(String voucherNo) throws Exception;

	/**
	 * @param voucherNo
	 * @param detailNo
	 * @return
	 * @throws Exception 取出TbVoucherDetail內符合voucherNo、detailNo的資料 若無則回傳null
	 */
	public List<TbVoucherDetail> findVoucherDetailByDetailNo(String detailNo, String voucherNo) throws Exception;

	/**
	 * @param TbVoucherDetail
	 * @return
	 * @throws Exception 儲存TbVoucherDetail
	 */
	public void save(TbVoucherDetail Object) throws Exception;

	/**
	 * @param TbVoucherDetail
	 * @return
	 * @throws Exception 修改TbVoucherDetail
	 */
	public void update(TbVoucherDetail Object) throws Exception;

	/**
	 * 
	 * @param voucherNo
	 * @return
	 * @throws Exception
	 */
	public List<TbVoucherDetail> findVoucherDetailByVoucherNoNDel(String voucherNo) throws Exception;

	/**
	 * 
	 * @param voucherNo
	 * @param company
	 * @param applicant
	 * @param status
	 * @param customer
	 * @param cusTaxId
	 * @param item
	 * @param projectId
	 * @return
	 * @throws Exception 取出TbVoucherDetail內符合帶入參數的資料 若無則回傳null
	 */
	public List<TbVoucherDetail> getVoucherDetailList(String voucherNo, String company, String applicant, String status,
			String customer, String cusTaxId, String item, String projectId) throws Exception;

}
