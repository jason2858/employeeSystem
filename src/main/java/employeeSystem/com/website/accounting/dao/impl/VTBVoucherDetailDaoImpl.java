package employeeSystem.com.website.accounting.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import employeeSystem.com.website.accounting.dao.VTbVoucherDetailDao;
import employeeSystem.com.website.accounting.model.VTbVoucherDetail;
import employeeSystem.com.website.system.util.HibernateUtil;

@Repository("VTbVoucherDetailDao")
public class VTBVoucherDetailDaoImpl extends BaseDao<VTbVoucherDetail> implements VTbVoucherDetailDao {

	@Override
	public List<VTbVoucherDetail> getVoucherDetailVList(String voucherNo, String company, String applicant,
			String status, String customer, String cusTaxId, String item, String projectId, String pageNo,
			String pageSize) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<VTbVoucherDetail> list = null;
		int pn = Integer.parseInt(pageNo);
		int ps = Integer.parseInt(pageSize);
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append("FROM VTbVoucherDetail WHERE 1=1");
			if (voucherNo != null) {
				hql.append(" AND voucher_no like '%" + voucherNo + "%'");
			}
			if (company != null) {
				hql.append(" AND company like '%" + company + "%'");
			}
			if (applicant != null) {
				hql.append(" AND applicant like '%" + applicant + "%'");
			}
			if (status != null) {
				hql.append(" AND status like '%" + status + "%'");
			}
			if (customer != null) {
				hql.append(" AND customer like '%" + customer + "%'");
			}
			if (cusTaxId != null) {
				hql.append(" AND cus_tax_id like '%" + cusTaxId + "%'");
			}
			if (item != null) {
				hql.append(" AND detail_item like '%" + item + "%'");
			}
			if (projectId != null) {
				if (projectId.equals("N")) {
					hql.append(" AND project = '' ");
				} else {
					hql.append(" AND project like '%" + projectId + "%'");
				}
			}
			hql.append("ORDER BY voucher_no DESC");
			Query<VTbVoucherDetail> query = session.createQuery(hql.toString(), VTbVoucherDetail.class)
					.setFirstResult((pn * ps) - ps).setMaxResults(ps);

			list = query.list();
		} finally {
//		session.close();
		}
		return list;
	}

	@Override
	public Map<String, Integer> getDetailTotal(String start, String end, String iId) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Integer total = 0;

		Map<String, Integer> result = new HashMap<String, Integer>();
		try {
			String hql = "SELECT sum(amount) as total FROM v_tb_voucher_detail where detail_item ='" + iId
					+ "' and status in('0','1','2') and credit_date between '" + start + "' and '" + end + "'";
			Query query = session.createSQLQuery(hql);
			Object sum = query.uniqueResult();
			if (StringUtils.isEmpty(sum)) {
				result.put("detailSum", total);
			} else {
				total = Integer.parseInt(sum.toString());

				result.put("detailSum", total);
			}
		} finally {
//			session.close();
		}
		return result;
	}
}
