package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.ProjectTypeDao;
import com.yesee.gov.website.model.TbProjectType;
import com.yesee.gov.website.util.HibernateUtil;

@Service("projectTypeDao")
public class ProjectTypeDaoImpl implements ProjectTypeDao {

	@Override
	public void save(TbProjectType Object) throws Exception {
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
	public void delete(TbProjectType Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
		} finally {
			//session.close();
		}

	}

	@Override
	public List<TbProjectType> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectType> list = null;
		String sql = null;

		try {
			sql = "FROM TbProjectType ";
			list = session.createQuery(sql, TbProjectType.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public TbProjectType findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbProjectType type = null;
		try {
			Transaction tx = session.beginTransaction();
			type = session.get(TbProjectType.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return type;
	}

}
