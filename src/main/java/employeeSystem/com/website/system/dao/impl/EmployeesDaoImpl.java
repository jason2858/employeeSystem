package com.yesee.gov.website.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.EmployeesDao;
import com.yesee.gov.website.model.TbEmployees;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("employeesDao")
public class EmployeesDaoImpl implements EmployeesDao {

	@Override
	public List<TbEmployees> getList(TbEmployees entity) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbEmployees> list = null;
		String hql = null;

		try {
			hql = "FROM TbEmployees WHERE 1=1 ";
			Map<String, Object> params = new HashMap<String, Object>();
			if (entity != null) {
				if (entity.getUsername() != null) {
					hql += " AND username = :name ";
					params.put("name", entity.getUsername());
				}
				if (entity.getGroupId() != null) {
					hql += " AND groupid = :gid ";
					params.put("gid", entity.getGroupId());
				}
				if (entity.getStatus() != null) {
					hql += " AND status = :nowstatus ";
					params.put("nowstatus", entity.getStatus());
				}
				if (entity.getDepartmentId() != null) {
					hql += " AND department_id = :depid ";
					params.put("depid", entity.getDepartmentId());
				}
			}
			Query<TbEmployees> query = session.createQuery(hql, TbEmployees.class);
			query.setProperties(params);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbEmployees> findByUserName(String name) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbEmployees> list = null;
		String hql = null;

		try {
			hql = "FROM TbEmployees WHERE username = :username";
			Query<TbEmployees> query = session.createQuery(hql, TbEmployees.class);
			query.setParameter("username", name);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbEmployees> findBydepId(List<String> ids, TbEmployees entity) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbEmployees> list = null;
		String hql = null;

		try {
			hql = "FROM TbEmployees WHERE departmentId IN :ids ";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ids", ids);
			if (entity != null) {
				if (entity.getUsername() != null) {
					hql += " AND username = :name ";
					params.put("name", entity.getUsername());
				}
				if (entity.getGroupId() != null) {
					hql += " AND groupid = :gid ";
					params.put("gid", entity.getGroupId());
				}
				if (entity.getStatus() != null) {
					hql += " AND status = :nowstatus ";
					params.put("nowstatus", entity.getStatus());
				}
			}
			Query<TbEmployees> query = session.createQuery(hql, TbEmployees.class);
			query.setProperties(params);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public void delete(TbEmployees Object) throws Exception {
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
	public void save(TbEmployees object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
		} finally {
			//session.close();
		}
	}

	@Override
	public List<TbEmployees> findHR(String account) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbEmployees> list = null;
		try {
			String HQL = "FROM TbEmployees WHERE departmentId = :departmentId and username != :USER";
			Query<TbEmployees> query = session.createQuery(HQL, TbEmployees.class);
			query.setParameter("USER", account);
			query.setParameter("departmentId", "15");
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

}
