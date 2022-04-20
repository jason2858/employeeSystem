package com.yesee.gov.website.dao.accounting.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.VoucherSignDao;
import com.yesee.gov.website.model.accounting.TbVoucherSign;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("VoucherSignDao")
public class VoucherSignDaoImpl extends BaseDao<TbVoucherSign> implements VoucherSignDao {

	@Override
	public List<TbVoucherSign> getVoucherSignListByVoucherNo(String voucherNo) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo.voucherNo", "%" + voucherNo + "%");
		List<String> order = new ArrayList<String>();
		order.add("entity.roleId");
		return super.findByHql(TbVoucherSign.class, param, null, order, true, 0, 0);
	}

	@Override
	public List<TbVoucherSign> getList(Map<String, Object> param, List<String> orderBy, Boolean desc) throws Exception {
		return super.findByHql(TbVoucherSign.class, param, null, null, false, 0, 0);
	}

	@Override
	public void delete(String voucherNo) throws Exception {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		try {
			String HQL = "DELETE FROM TbVoucherSign entity WHERE entity.voucherNo.voucherNo = :voucherNo";
			Query query = session.createQuery(HQL);
			query.setParameter("voucherNo", voucherNo);
			int count = query.executeUpdate();
			System.out.println("Deleting with voucherNo: " + voucherNo);
			System.out.println(count + " Record(s) Deleted.");
			tx.commit();
		} finally {
			// session.close();
		}
	}

}
