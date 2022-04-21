package employeeSystem.com.website.accounting.dao;

import java.util.List;

import employeeSystem.com.website.accounting.model.TbVoucherModifyAuth;

public interface VoucherModifyAuthDao {

	/**
	 * @param tbVoucherModifyAuth
	 * @return
	 * @throws Exception 儲存tbVoucherModifyAuth
	 */
	public void save(TbVoucherModifyAuth tbVoucherModifyAuth) throws Exception;

	public void update(TbVoucherModifyAuth tbVoucherModifyAuth) throws Exception;

	/**
	 * @param voucherNo
	 * @param account
	 * @return
	 * @throws Exception 取出TbVoucherModifyAuth內符合voucherNo、account的最新資料 若無則回傳null
	 */
	public List<TbVoucherModifyAuth> findVoucherModifyAuthByVNo(String voucherNo, String account) throws Exception;

	/**
	 * @param voucherNo
	 * @param token
	 * @return
	 * @throws Exception 取出TbVoucherModifyAuth內符合voucherNo的資料 若無則回傳null
	 */
	public List<TbVoucherModifyAuth> findVoucherModifyAuthByVNoAndToken(String voucherNo, String token)
			throws Exception;

}
