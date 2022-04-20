package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.DepartmentDao;
import com.yesee.gov.website.model.TbDepartment;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("departmentDao")
public class DepartmentDaoImpl implements DepartmentDao {

	@Override
	public void save(TbDepartment Object) throws Exception {
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
	public void updateAuthorise(String authorise, String manager) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			String HQL = "UPDATE TbEmployees SET group_id = :AUTHORISE WHERE username = :MANAGER";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("AUTHORISE", authorise);
			query.setParameter("MANAGER", manager);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}

	@Override
	public void delete(TbDepartment Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//session.close();
		}

	}

	@Override
	public List<TbDepartment> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbDepartment> list = null;
		String sql = null;
		try {
			sql = "FROM TbDepartment ";
			list = session.createQuery(sql, TbDepartment.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public TbDepartment findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbDepartment dep = null;
		try {
			Transaction tx = session.beginTransaction();
			dep = (TbDepartment) session.get(TbDepartment.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return dep;
	}

	@Override
	public List<TbDepartment> findByDepartmentIds(List<Integer> ids) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbDepartment> list = null;
		try {
			String hql = "FROM TbDepartment WHERE id IN :ids";
			Query<TbDepartment> query = session.createQuery(hql, TbDepartment.class);
			query.setParameter("ids", ids);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbDepartment> findByCompany(String id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbDepartment> list = null;
		try {
			String hql = "FROM TbDepartment WHERE companyId = :id";
			Query<TbDepartment> query = session.createQuery(hql, TbDepartment.class);
			query.setParameter("id", id);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbDepartment> findByManager(String account) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbDepartment> list = null;
		try {
			String hql = "FROM TbDepartment WHERE manager = :account";
			Query<TbDepartment> query = session.createQuery(hql, TbDepartment.class);
			query.setParameter("account", account);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}
}
