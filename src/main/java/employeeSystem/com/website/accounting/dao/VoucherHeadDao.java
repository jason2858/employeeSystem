package com.yesee.gov.website.dao.accounting;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.accounting.TbVoucherHead;

public interface VoucherHeadDao {
	

	/**
	 * @param voucherNo
	 * @return
	 * @throws Exception 取出TbVoucherHead內符合voucherNo的資料 若無則回傳null
	 */
	public List<TbVoucherHead> findVoucherHeadByVoucherNo(String voucherNo) throws Exception;

	/**
	 * @param date
	 * @return
	 * @throws Exception 取出TbVoucherHead內當日voucher_no最大值 若無則回傳null
	 */
	public List<TbVoucherHead> findVoucherHeadByVoucherNoDate(String date) throws Exception;

	/**
	 * @param Object
	 * @return
	 * @throws Exception 儲存TbVoucherHead內容
	 */
	public void save(TbVoucherHead Object) throws Exception;

	/**
	 * @param name
	 * @param account
	 * @return
	 * @throws Exception 取出TbVoucherHead內符合name、account的最新資料 若無則回傳null
	 */
	public List<TbVoucherHead> findNewestVoucherHead(String name, String account) throws Exception;

	/**
	 * @param date
	 * @param voucherNo
	 * @param voucherName
	 * @param company
	 * @param creditDateF
	 * @param creditDateT
	 * @param predictDateF
	 * @param predictDateT
	 * @param applicant
	 * @param status
	 * @return
	 * @throws Exception 取出TbVoucherHead內符合帶入參數的資料 若無則回傳null
	 */
	public List<TbVoucherHead> getVoucherHeadList(String voucherNo, String voucherName, String company,
			String creditDateF, String creditDateT, String predictDateF, String predictDateT, String applicant,
			String status) throws Exception;

	/**
	 * @param Object
	 * @return
	 * @throws Exception 儲存TbVoucherHead內容
	 */
	public void update(TbVoucherHead Object) throws Exception;

	public Map<String, Integer> getHeadTotal(String start, String end, String iId);

}
