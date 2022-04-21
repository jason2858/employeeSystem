package employeeSystem.com.website.accounting.dao;

import java.util.List;

import employeeSystem.com.website.accounting.model.TbVoucherCommon;

public interface VoucherCommonDao {

	/**
	 * @throws Exception 取得TbVoucherCommon內所有資料
	 */
	public List<TbVoucherCommon> getVoucherCommonList() throws Exception;

	/**
	 * @return
	 * @TbVoucherCommon Object
	 * @throws Exception 儲存常用傳票清單
	 */
	public void save(TbVoucherCommon Object) throws Exception;

	/**
	 * @param voucherName
	 * @return
	 * @throws Exception 取出TbVoucherCommon內符合voucherName的資料若無則回傳null
	 */
	public List<TbVoucherCommon> findVoucherCommonListByVoucherName(String voucherName) throws Exception;

}
