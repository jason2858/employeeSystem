package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.VTbVoucherHedgeDao;
import com.yesee.gov.website.model.accounting.VTbVoucherHedge;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("VTbVoucherHedgeDao")
public class VTbVoucherHedgeDaoImpl extends BaseDao<VTbVoucherHedge> implements VTbVoucherHedgeDao {

	@Override
	public List<VTbVoucherHedge> getList(Map<String, Object> param) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<VTbVoucherHedge> result = null;
		StringBuffer hql = new StringBuffer();

		hql.append(" From VTbVoucherHedge entity");
		hql.append(" where 1 = 1 ");
		param.forEach((k, v) -> {
			if (k.equals("start")) {
				hql.append(" and ").append("entity.creditDate").append(" >= :").append(k);
			} else if (k.equals("end")) {
				hql.append(" and ").append("entity.creditDate").append(" <= :").append(k);
			} else {
				hql.append(" and ").append(k).append(" = :").append(k.substring(k.lastIndexOf(".") + 1));
			}
		});

		try {
			Query<VTbVoucherHedge> query = session.createQuery(hql.toString(), VTbVoucherHedge.class);
			param.forEach((k, v) -> query.setParameter(k.substring(k.lastIndexOf(".") + 1), v));
			result = query.list();
		} finally {
			// session.close();
		}
		return result;
	}

}
