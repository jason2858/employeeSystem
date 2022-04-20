package com.yesee.gov.website.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.ProjectAssistDao;
import com.yesee.gov.website.model.TbProjectAssist;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("projectAssistDao")
public class ProjectAssistDaoImpl implements ProjectAssistDao {

	@Override
	public void save(TbProjectAssist Object) throws Exception {
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
	public void delete(TbProjectAssist Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
		} finally {
			//session.close();
		}

	}

}
