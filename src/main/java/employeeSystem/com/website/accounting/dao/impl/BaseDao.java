package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import com.yesee.gov.website.dao.accounting.IBaseDao;
import com.yesee.gov.website.util.HibernateUtil;

public class BaseDao<T> implements IBaseDao<T> {

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(T t) throws Exception {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(t);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (session.isConnected()) {
				// session.close();
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(T t) throws Exception {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(session.merge(t));
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (session.isConnected()) {
				// session.close();
			}
		}
	}

	@Override
	public T findById(Class<T> t, Object id) throws Exception {
		Session session = HibernateUtil.getSession();
		try {
			return (T) session.find(t, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (session.isConnected()) {
				// session.close();
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(T t) throws Exception {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		try {
			session.delete(session.merge(t));
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			throw e;
		} finally {
			if (session.isConnected()) {
				// session.close();
			}
		}
	}

	@Override
	public List<T> findByHql(Class<T> t, Map<String, Object> param, List<String> groupBy, List<String> orderBy,
			boolean desc, int offset, int limit) throws Exception {
		Session session = HibernateUtil.getSession();
		StringBuffer hql = new StringBuffer();
		List<T> result = null;
		String entityName = t.getName();
		hql.append(" From " + entityName + " entity");
		hql.append(" where 1 = 1 ");
//		param.forEach((k, v) -> hql.append(" and ").append(k)
//				.append((v instanceof String) ? v.toString().indexOf("%") < 0 ? " = :" : " like :"
//						: (v instanceof List) ? " in (:" : "")
//				.append(k.substring(k.lastIndexOf(".") + 1)).append((v instanceof List) ? ")" : ""));
		param.forEach((k, v) ->hql.append(" and ").append(k)
		.append((v instanceof String) ? v.toString().indexOf("%") < 0
				? (v.toString().indexOf("&&&") < 0 ? " = :" : " between :")
				: " like :" : (v instanceof List) ? " in (:" : "")
		.append(k.substring(k.lastIndexOf(".") + 1))
		.append((v instanceof List) ? ")"
				: (v.toString().indexOf("&&&") < 0 ? ""
						: ("1 and :" + k.substring(k.lastIndexOf(".") + 1) + 2))));
		if (groupBy != null) {
			hql.append(" group by ");
			groupBy.forEach((v) -> hql.append(v).append(","));
			hql.delete(hql.length() - 1, hql.length());
		}

		if (orderBy != null) {
			hql.append(" order by ");
			orderBy.forEach((v) -> hql.append(v).append(","));
			hql.delete(hql.length() - 1, hql.length());

			if (desc) {
				hql.append(" desc ");
			}
		}

		try {
			Query<T> query = (Query<T>) session.createQuery(hql.toString(), t);
//			param.forEach((k, v) -> query.setParameter(k.substring(k.lastIndexOf(".") + 1), v));
			param.forEach((k, v)->{
				if(v.toString().indexOf("&&&") >0) {
					query.setParameter(k.substring(k.lastIndexOf(".") + 1)+1, v.toString().substring(0, v.toString().indexOf("&&&")));
					query.setParameter(k.substring(k.lastIndexOf(".") + 1)+2, v.toString().substring(v.toString().indexOf("&&&")+3));
				}else {
					query.setParameter(k.substring(k.lastIndexOf(".") + 1), v);
				}
			});
			if (limit > 0) {
				query.setFirstResult(offset);
				query.setMaxResults(limit);
			}
			result = query.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	@Override
	public List<Object[]> findBySql(List<String> column, Map<String, Object> param) throws Exception {
		// TODO
		return null;
	}
}