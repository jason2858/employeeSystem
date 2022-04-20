package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.CustomerDao;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("customerDao")
public class CustomerDaoImpl implements CustomerDao {

	@Override
	public void save(TbCustomer Object) throws Exception {
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
	public void delete(TbCustomer Object) throws Exception {
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
	public void removeParentId(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			String HQL = "UPDATE TbCustomer SET parent_id = NULL WHERE parent_id = :ID";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("ID", id);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}

	@Override
	public TbCustomer findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;
		try {
			hql = "FROM TbCustomer WHERE id = :id";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("id", id);
			list = query.list();
		} finally {
			//session.close();
		}
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public List<TbCustomer> getVisibleList(String account) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;

		try {
			hql = "FROM TbCustomer WHERE status = :status OR (creator = :creator AND status <> :delete)";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("status", "signed");
			query.setParameter("creator", account);
			query.setParameter("delete", "delete");
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public Integer getUnsignCount() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbCustomer WHERE status = :status";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("status", "unsign");
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}

	@Override
	public List<TbCustomer> getListByNotEqualsStatus(String status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;
		try {
			hql = "FROM TbCustomer WHERE status != :status ";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("status", status);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbCustomer> getListByStatus(String status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;
		try {
			hql = "FROM TbCustomer WHERE status = :status ";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("status", status);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public TbCustomer findByName(String name) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;
		try {
			hql = "FROM TbCustomer WHERE name = :name";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("name", name);
			list = query.list();
		} finally {
			//session.close();
		}
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public TbCustomer findByEin(String cusTaxId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCustomer> list = null;
		String hql = null;
		try {
			hql = "FROM TbCustomer WHERE ein = :cusTaxId";
			Query<TbCustomer> query = session.createQuery(hql, TbCustomer.class);
			query.setParameter("cusTaxId", cusTaxId);
			list = query.list();
		} finally {
			//session.close();
		}
		return list.size() > 0 ? list.get(0) : null;
	}

}
