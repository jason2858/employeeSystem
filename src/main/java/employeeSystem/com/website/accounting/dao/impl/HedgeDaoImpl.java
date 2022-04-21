package employeeSystem.com.website.accounting.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.HedgeDao;
import employeeSystem.com.website.accounting.model.TbHedge;
import employeeSystem.com.website.system.util.HibernateUtil;

@Repository("HedgeDao")
public class HedgeDaoImpl extends BaseDao<TbHedge> implements HedgeDao {

	@Override
	public List<TbHedge> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbHedge.class, param, null, null, false, 0, 0);
	}

	@Override
	public TbHedge findById(String id) throws Exception {
		return super.findById(TbHedge.class, id);
	}

	@Override
	public List<TbHedge> findHedgeNoByVoucherNo(String voucherNo) throws Exception {

		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbHedge> list = null;
		try {
			Transaction tx = session.beginTransaction();
			Query Q = null;
			String query1 = "SELECT max(hedgeNo) FROM TbHedge h WHERE h.hedgeNo LIKE :no";
			Q = session.createQuery(query1);
			Q.setParameter("no", voucherNo + "%");
			String D = (String) Q.uniqueResult();
			String HQL = "FROM TbHedge WHERE hedge_no =  :no ";
			Query<TbHedge> query = session.createQuery(HQL, TbHedge.class);
			query.setParameter("no", D);
			list = query.list();
		} finally {
//			session.close();
		}
		return list;
	}
}
