package com.yesee.gov.website.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.PunchRecordsDao;
import com.yesee.gov.website.model.TbPunchRecords;
import com.yesee.gov.website.util.DateUtil;
import com.yesee.gov.website.util.HibernateUtil;

@Service("punchRecordsDao")
public class PunchRecordsDaoImpl implements PunchRecordsDao {

	@Override
	public List<TbPunchRecords> findPunchStatus(String user, String type) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user = :USER AND (type = :TYPE OR type = :MAKEUPTYPE) AND DATE(punch_time) = :DAY AND (status='CREATED' OR status='SIGNED') ORDER BY punch_time DESC";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USER", user);
			query.setParameter("TYPE", type);
			query.setParameter("MAKEUPTYPE", "makeup" + type);
			query.setParameter("DAY", new Date());
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbPunchRecords> findMakeUpCount(String user, String startDate, String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user = :USER AND punch_time BETWEEN :STARTDATE AND :ENDDATE AND (type ='makeupin' OR type = 'makeupout') AND (status='CREATED' OR status='SIGNED')";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USER", user);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbPunchRecords> getRecords(String user, String startDate, String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user = :USER AND punch_time BETWEEN :STARTDATE AND :ENDDATE AND status != 'CANCELLED' AND status!='REJECTED' ORDER BY punch_time DESC";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USER", user);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbPunchRecords> findPunch(TbPunchRecords record) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user = :USER AND (type = :TYPE OR type = :MAKEUPTYPE) AND DATE(punch_time) = :DAY AND (status='CREATED' OR status='SIGNED')";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USER", record.getTbEmployeesByUser().getUsername());
			query.setParameter("TYPE", record.getType());
			query.setParameter("MAKEUPTYPE", "makeup" + record.getType());
			query.setParameter("DAY", record.getPunchTime());
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbPunchRecords> findMakeUp(TbPunchRecords record) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user = :USER AND type = :TYPE AND DATE(punch_time) = :DAY AND status='CREATED'";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USER", record.getTbEmployeesByUser().getUsername());
			query.setParameter("TYPE", record.getType());
			query.setParameter("DAY", record.getPunchTime());
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public void save(TbPunchRecords record) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(record);
			tx.commit();
		} finally {
			//session.close();
		}
	}

	@Override
	public void del(Long id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			String HQL = "UPDATE TbPunchRecords SET status='CANCELLED' WHERE id = :ID";
			Query<?> query = session.createQuery(HQL);
			query.setParameter("ID", id);
			query.executeUpdate();
			tx.commit();
		} finally {
			//session.close();
		}
	}

	@Override
	public TbPunchRecords findById(Long id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbPunchRecords pun = null;
		try {
			Transaction tx = session.beginTransaction();
			pun = (TbPunchRecords) session.get(TbPunchRecords.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return pun;
	}

	@Override
	public Integer getUnsignCount(List<String> users) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbPunchRecords WHERE user IN :users AND (type = :makeupin or type = :makeupout) AND status = :status";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("users", users);
			query.setParameter("makeupin", "makeupin");
			query.setParameter("makeupout", "makeupout");
			query.setParameter("status", "CREATED");
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}

	@Override
	public List<TbPunchRecords> findMakeUpByStatus(List<String> nameList, String status, String startDate,
			String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user IN (:USERS) AND status = :STATUS AND punch_time BETWEEN :STARTDATE AND :ENDDATE AND (type = 'makeupin' OR type = 'makeupout')";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USERS", nameList);
			query.setParameter("STATUS", status);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbPunchRecords> findAllMakeUpByStatus(List<String> nameList, String status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPunchRecords> list = null;
		try {
			String HQL = "FROM TbPunchRecords WHERE user IN (:USERS) AND status = :STATUS AND (type = 'makeupin' OR type = 'makeupout')";
			Query<TbPunchRecords> query = session.createQuery(HQL, TbPunchRecords.class);
			query.setParameter("USERS", nameList);
			query.setParameter("STATUS", status);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

}