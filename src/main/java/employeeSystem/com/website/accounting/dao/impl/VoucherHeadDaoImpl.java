package employeeSystem.com.website.accounting.dao.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import employeeSystem.com.website.accounting.dao.VoucherHeadDao;
import employeeSystem.com.website.accounting.model.TbVoucherHead;
import employeeSystem.com.website.system.util.HibernateUtil;

@Repository("VoucherHeadDao")
public class VoucherHeadDaoImpl extends BaseDao<TbVoucherHead> implements VoucherHeadDao {

	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
	String now = sdFormat.format(currentTimestamp);

	@Override
	public List<TbVoucherHead> findVoucherHeadByVoucherNo(String voucherNo) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("entity.voucherNo", voucherNo);

		return super.findByHql(TbVoucherHead.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherHead> findVoucherHeadByVoucherNoDate(String date) throws Exception {

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbVoucherHead> list = null;
		try {
			Transaction tx = session.beginTransaction();
			Query Q = null;
			String query1 = "SELECT max(voucherNo) FROM TbVoucherHead h WHERE h.voucherNo LIKE :no";
			Q = session.createQuery(query1);
			Q.setParameter("no", date + "%");
			String D = (String) Q.uniqueResult();
			String HQL = "FROM TbVoucherHead WHERE voucher_no =  :no ";
			Query<TbVoucherHead> query = session.createQuery(HQL, TbVoucherHead.class);
			query.setParameter("no", D);
			list = query.list();
		} finally {
//			session.close();
		}
		return list;
	}

	@Override
	public List<TbVoucherHead> findNewestVoucherHead(String name, String account) throws Exception {

		Map<String, Object> param = new HashMap<String, Object>();

		List<String> order = new ArrayList<>();

		param.put("entity.voucherName", name);

		param.put("entity.createUser", account);

		order.add("entity.createDate");

		return super.findByHql(TbVoucherHead.class, param, null, order, true, 0, 0);
	}

	@Override
	public List<TbVoucherHead> getVoucherHeadList(String voucherNo, String voucherName, String company,
			String creditDateF, String creditDateT, String predictDateF, String predictDateT, String applicant,
			String status) throws Exception {

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbVoucherHead> list = null;
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append("FROM TbVoucherHead WHERE 1=1");
			if (voucherNo != null) {
				hql.append(" AND voucher_no like '%" + voucherNo + "%'");
			}
			if (voucherName != null) {
				hql.append(" AND voucher_name like '%" + voucherName + "%'");
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
			if (creditDateF != null || creditDateT != null) {
				if (creditDateF == null) {
					hql.append(" AND credit_date BETWEEN '20000101' AND '" + creditDateT + "'");
				} else if (creditDateT == null) {
					hql.append(" AND credit_date BETWEEN '" + creditDateF + "' AND '" + now + "'");
				} else {
					hql.append(" AND credit_date BETWEEN '" + creditDateF + "' AND '" + creditDateT + "'");
				}
			}
			if (predictDateF != null || predictDateT != null) {
				if (predictDateF == null) {
					hql.append(" AND predict_date BETWEEN '19110101' AND '" + predictDateT + "'");
				} else if (predictDateT == null) {
					hql.append(" AND predict_date BETWEEN '" + predictDateF + "' AND '" + now + "'");
				} else {
					hql.append(" AND predict_date BETWEEN '" + predictDateF + "' AND '" + predictDateT + "'");
				}
			}
			Query<TbVoucherHead> query = session.createQuery(hql.toString(), TbVoucherHead.class);

			list = query.list();
		} finally {
//			session.close();
		}

		return list;
	}

	@Override
	public Map<String, Integer> getHeadTotal(String start, String end, String iId) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		int total = 0;

		Map<String, Integer> result = new HashMap<String, Integer>();
		try {

			String hql = "SELECT SUM(amount_total) AS total FROM tb_voucher_head WHERE head_item ='" + iId
					+ "' AND status IN('0','1','2') AND credit_date BETWEEN '" + start + "' AND '" + end + "'";

			Query query = session.createSQLQuery(hql);

			Object sum = query.uniqueResult();
			if (StringUtils.isEmpty(sum)) {
				result.put("headSum", total);
			} else {
				result.put("headSum", Integer.parseInt(sum.toString()));
			}
		} finally {
//			session.close();
		}
		return result;
	}

}
