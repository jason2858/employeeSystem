package employeeSystem.com.website.accounting.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.VoucherHedgeDao;
import com.yesee.gov.website.model.accounting.TbVoucherHedge;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("VoucherHedgeDao")
public class VoucherHedgeDaoImpl extends BaseDao<TbVoucherHedge> implements VoucherHedgeDao {

	@Override
	public List<TbVoucherHedge> getList(Map<String, Object> param) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbVoucherHedge> result = null;
		StringBuffer hql = new StringBuffer();

		hql.append(" From TbVoucherHedge entity");
		hql.append(" where 1 = 1 ");
		param.forEach((k, v) -> {
			if (k.equals("start")) {
				hql.append(" and ").append("entity.creditDate").append(" > :").append(k);
			} else if (k.equals("end")) {
				hql.append(" and ").append("entity.creditDate").append(" < :").append(k);
			} else {
				hql.append(" and ").append(k).append(" = :").append(k.substring(k.lastIndexOf(".") + 1));
			}
		});

		try {
			Query<TbVoucherHedge> query = session.createQuery(hql.toString(), TbVoucherHedge.class);
			param.forEach((k, v) -> query.setParameter(k.substring(k.lastIndexOf(".") + 1), v));
			result = query.list();
		} finally {
			// session.close();
		}
		return result;
	}

	@Override
	public List<TbVoucherHedge> findByHedgeNo(Map<String, Object> param) throws Exception {
		return super.findByHql(TbVoucherHedge.class, param, null, null, false, 0, 0);
	}

	@Override
	public List<TbVoucherHedge> findToday() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbVoucherHedge> list = null;
		try {
			String hql = "FROM TbVoucherHedge WHERE date(createDate)=CURDATE() ORDER BY hVoucherNo desc , createDate desc";
			Query<TbVoucherHedge> query = session.createQuery(hql, TbVoucherHedge.class);
			list = query.list();
		} finally {
			// session.close();
		}
		return list;
	}

}
