package com.yesee.gov.website.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.SignCodeDao;
import com.yesee.gov.website.model.TbSignCode;
import com.yesee.gov.website.util.HibernateUtil;

@Service("signCodeDao")
public class SignCodeDaoImpl implements SignCodeDao {

	@Override
	public TbSignCode findByCode(String code) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSignCode> list = new ArrayList<>();
		String hql = null;

		try {
			hql = "FROM TbSignCode WHERE hashcode = :code ";
			Query<TbSignCode> query = session.createQuery(hql, TbSignCode.class);
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
	public void save(TbSignCode Object) throws Exception {
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
			String HQL = "DELETE FROM TbSignCode where schedule_id = : ID";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("ID", id);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}
}