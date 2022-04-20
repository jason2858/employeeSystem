package com.yesee.gov.website.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.DeleteCodeDao;
import com.yesee.gov.website.model.TbDeleteCode;
import com.yesee.gov.website.util.HibernateUtil;

@Service("deleteCodeDao")
public class DeleteCodeDaoImpl implements DeleteCodeDao {

	@Override
	public TbDeleteCode findByCode(String code) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbDeleteCode> list = new ArrayList<>();
		String hql = null;

		try {
			hql = "FROM TbDeleteCode WHERE hashcode = :code ";
			Query<TbDeleteCode> query = session.createQuery(hql, TbDeleteCode.class);
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
	public void save(TbDeleteCode Object) throws Exception {
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
			String HQL = "DELETE FROM TbDeleteCode where schedule_id = : ID";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("ID", id);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}
}