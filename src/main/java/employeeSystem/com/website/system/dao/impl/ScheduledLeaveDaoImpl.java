package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.ScheduledLeaveDao;
import com.yesee.gov.website.model.TbScheduledLeave;
import com.yesee.gov.website.model.TbScheduledLeaveId;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("scheduledLeaveDao")
public class ScheduledLeaveDaoImpl implements ScheduledLeaveDao {

	@Override
	public void save(TbScheduledLeave object) throws Exception {
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
	public void delete(TbScheduledLeave Object) throws Exception {
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
	public List<TbScheduledLeave> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbScheduledLeave> list = null;
		String hql = null;

		try {
			hql = "FROM TbScheduledLeave ";
			list = session.createQuery(hql, TbScheduledLeave.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbScheduledLeave> findById(TbScheduledLeaveId id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbScheduledLeave> list = null;
		String hql = null;
		String empName = id.getEmpName();
		String skdDate = id.getSkdDate();

		try {
			hql = "FROM TbScheduledLeave  s WHERE s.id.empName = :empName AND s.id.skdDate = :skdDate ";
			Query<TbScheduledLeave> query = session.createQuery(hql, TbScheduledLeave.class);
			query.setParameter("empName", empName);
			query.setParameter("skdDate", skdDate);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbScheduledLeave> findByEmployeesName(String empName) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbScheduledLeave> list = null;
		String hql = null;

		try {
			hql = "FROM TbScheduledLeave  s WHERE s.id.empName = :empName ";
			Query<TbScheduledLeave> query = session.createQuery(hql, TbScheduledLeave.class);
			query.setParameter("empName", empName);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbScheduledLeave> findByYearAndEmployees(String year, String empName) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbScheduledLeave> list = null;
		String hql = null;

		try {
			hql = "FROM TbScheduledLeave  s WHERE s.id.skdDate like concat('%',:year,'%') AND s.id.empName = :empName ";
			Query<TbScheduledLeave> query = session.createQuery(hql, TbScheduledLeave.class);
			query.setParameter("year", year);
			query.setParameter("empName", empName);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

}
