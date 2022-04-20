package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.AnnualLeaveDao;
import com.yesee.gov.website.model.TbAnnualLeave;
import com.yesee.gov.website.model.TbAnnualLeaveId;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("annualLeaveDao")
public class AnnualLeaveDaoImpl implements AnnualLeaveDao {

	private static final Logger logger = LogManager.getLogger(AnnualLeaveDaoImpl.class);

	@Override
	public void save(TbAnnualLeave object) throws Exception {
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
	public void delete(TbAnnualLeave Object) throws Exception {
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
	public List<TbAnnualLeave> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnualLeave> list = null;
		String hql = null;

		try {
			hql = "FROM TbAnnualLeave ";
			list = session.createQuery(hql, TbAnnualLeave.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbAnnualLeave> findById(TbAnnualLeaveId tbAnnuaLeaveId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnualLeave> list = null;
		String hql = null;
		String empName = tbAnnuaLeaveId.getEmpName();
		String year = tbAnnuaLeaveId.getYear();

		try {
			hql = "FROM TbAnnualLeave  a WHERE a.id.empName = :empName " + " AND a.id.year = :year ";
			Query<TbAnnualLeave> query = session.createQuery(hql, TbAnnualLeave.class);
			query.setParameter("empName", empName);
			query.setParameter("year", year);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbAnnualLeave> getListByYear(int year) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnualLeave> list = null;
		String hql = null;

		try {
			hql = "FROM TbAnnualLeave a where a.id.year = :YEAR";
			Query<TbAnnualLeave> query = session.createQuery(hql, TbAnnualLeave.class);
			query.setParameter("YEAR", year + "");
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbAnnualLeave> findByNameAndYear(String account, String year) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnualLeave> list = null;
		try {
			String HQL = "FROM TbAnnualLeave  a WHERE a.id.empName = :USER " + " AND a.id.year = :YEAR ";
			Query<TbAnnualLeave> query = session.createQuery(HQL, TbAnnualLeave.class);
			query.setParameter("USER", account);
			query.setParameter("YEAR", year);
			list = query.list();
			logger.info("find annualLeave by name and year success size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbAnnualLeave> getListByEmployees(List<String> empOfCompany) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnualLeave> list = null;
		try {
			String HQL = "FROM TbAnnualLeave  a WHERE a.id.empName IN :USERS";
			Query<TbAnnualLeave> query = session.createQuery(HQL, TbAnnualLeave.class);
			query.setParameter("USERS", empOfCompany);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}
}
