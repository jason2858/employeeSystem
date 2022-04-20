package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.RejectedCodeDao;
import com.yesee.gov.website.model.TbRejectCode;
import com.yesee.gov.website.util.HibernateUtil;

@Service("rejectedCodeDao")
public class RejectedCodeDaoImpl implements RejectedCodeDao {

	@Override
	public TbRejectCode findByCode(String code) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbRejectCode> list = null;
		String hql = null;

		try {
			hql = "FROM TbRejectCode WHERE hashcode = :code ";
			Query<TbRejectCode> query = session.createQuery(hql, TbRejectCode.class);
			query.setParameter("code", code);
			list = query.list();
		} finally {
			//session.close();
		}
		if (list.size() != 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void save(TbRejectCode Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(Object);
			tx.commit();
		} finally {
			//session.close();
		}

	}

	@Override
	public void deleteBySchdulesId(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			String HQL = "DELETE FROM TbRejectCode where schedule_id = : ID";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("ID", id);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}
}