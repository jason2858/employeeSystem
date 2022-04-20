package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yesee.gov.website.exception.AccountingException;
import com.yesee.gov.website.model.accounting.TbVoucherSign;
import com.yesee.gov.website.pojo.accounting.InsertVoucherInfo;
import com.yesee.gov.website.pojo.accounting.UpdateVoucherInfo;

import net.sf.json.JSONObject;

public interface VoucherService {

	/**
	 * @return String 取得常用傳票清單
	 * @throws AccountingException Exception
	 */
	public String getVoucherCommonList(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception;

	/**
	 * @return String 儲存傳票
	 * @throws AccountingException Exception
	 */
	public String saveVoucher(HttpServletRequest req, HttpServletResponse resp, InsertVoucherInfo voucherInfo)
			throws AccountingException, Exception;

	/**
	 * @return String 更新傳票
	 * @throws AccountingException Exception
	 */
	public String updateVoucher(HttpServletRequest req, HttpServletResponse resp, UpdateVoucherInfo voucherInfo)
			throws AccountingException, Exception;

	/**
	 * @return 檢查傳票是否已設定簽程，更新傳票內容
	 * @throws Exception
	 */
	public String sendVoucher(HttpServletRequest req, HttpServletResponse resp, JSONObject voucherInfo)
			throws AccountingException, Exception;

	/**
	 * @return 取得傳票內容
	 * @throws Exception
	 */
	public String getVoucher(HttpServletRequest req, HttpServletResponse resp) throws AccountingException, Exception;

	/**
	 * @return 取得傳票表頭
	 * @throws Exception
	 */
	public String getVoucherHead(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception;

	/**
	 * @return 取得傳票明細
	 * @throws Exception
	 */
	public String getVoucherDetail(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception;

	/**
	 * @return 取得傳票修改權限
	 * @throws Exception
	 */
	public String getVoucherMToken(HttpServletRequest req, HttpServletResponse resp)
			throws AccountingException, Exception;

	/**
	 * @return 刪除傳票明細
	 * @throws Exception
	 */
	public String deleteVoucher(HttpServletRequest req, HttpServletResponse resp, JSONObject voucherInfo)
			throws AccountingException, Exception;

	public void sendMail(String voucherNo, TbVoucherSign tbVoucherSign) throws Exception;
}
