package com.yesee.gov.website.dao.accounting.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesee.gov.website.dao.accounting.VoucherDetailDao;
import com.yesee.gov.website.model.accounting.TbVoucherDetail;
import com.yesee.gov.website.model.accounting.TbVoucherHead;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("VoucherDetailDao")
public class VoucherDetailDaoImpl extends BaseDao<TbVoucherDetail> implements VoucherDetailDao {

	@Override
	public List<TbVoucherDetail> getList(Map<String, Object> param) throws Exception {

		return super.findByHql(TbVoucherDetail.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherDetail> findVoucherDetailByVoucherNo(String voucherNo) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo.voucherNo", voucherNo);
		param.put("entity.isDel", "N");
		return super.findByHql(TbVoucherDetail.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherDetail> findVoucherDetailByVoucherNoNDel(String voucherNo) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo.voucherNo", voucherNo);
		return super.findByHql(TbVoucherDetail.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherDetail> findVoucherDetailByDetailNo(String detailNo, String voucherNo) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo.voucherNo", voucherNo);
		param.put("entity.detailNo", detailNo);
		param.put("entity.isDel", "N");
		return super.findByHql(TbVoucherDetail.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherDetail> getVoucherDetailList(String voucherNo, String company, String applicant, String status,
			String customer, String cusTaxId, String item, String projectId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbVoucherDetail> list = null;
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append(
					"SELECT d FROM TbVoucherDetail d LEFT JOIN d.voucherNo h ON(d.voucherNo =h.voucherNo) WHERE 1=1");
			if (voucherNo != null) {
				hql.append(" AND d.voucher_no = '" + voucherNo + "'");
			}
			if (company != null) {
				hql.append(" AND h.company = '" + company + "'");
			}
			if (applicant != null) {
				hql.append(" AND h.applicant = '" + applicant + "'");
			}
			if (status != null) {
				hql.append(" AND h.status = '" + status + "'");
			}
			if (customer != null) {
				hql.append(" AND h.customer = '" + customer + "'");
			}
			if (cusTaxId != null) {
				hql.append(" AND h.cus_tax_id = '" + cusTaxId + "'");
			}
			if (item != null) {
				hql.append(" AND d.detail_item = '" + item + "'");
			}
			if (projectId != null) {
				if (projectId == "N") {
					hql.append(" AND d.project_id = null ");
				} else {
					hql.append(" AND d.project_id = '" + projectId + "'");
				}
			}

			Query<TbVoucherDetail> query = session.createQuery(hql.toString(), TbVoucherDetail.class);

			list = query.list();
		} finally {
//		session.close();
		}
		return list;
	}


}
