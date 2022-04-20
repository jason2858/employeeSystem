package com.yesee.gov.website.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.WorkItemDao;
import com.yesee.gov.website.model.TbWorkItem;
import com.yesee.gov.website.util.HibernateUtil;

@Service("workItemDao")
public class WorkItemDaoImpl implements WorkItemDao {

	private static final Logger logger = LogManager.getLogger(WorkItemDao.class);

	@Override
	public List<TbWorkItem> findHour(String account, String date) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE emp_name = :USER AND date = DATE(:DATE) and STATUS != 'CANCELLED'";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("USER", account);
			query.setParameter("DATE", date);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbWorkItem> findRecords(String account, Date startDate, Date endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE emp_name = :USER AND date BETWEEN :STARTDATE AND :ENDDATE AND status != 'CANCELLED' ORDER BY created_at DESC";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("USER", account);
			query.setParameter("STARTDATE", startDate);
			query.setParameter("ENDDATE", endDate);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbWorkItem> findRecordsByList(List<String> accounts, Date startDate, Date endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE emp_name IN :USERS AND date BETWEEN :STARTDATE AND :ENDDATE AND status != 'CANCELLED' ORDER BY created_at DESC";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("USERS", accounts);
			query.setParameter("STARTDATE", startDate);
			query.setParameter("ENDDATE", endDate);
			list = query.list();
			logger.info("get TbWorkItem in List<String> sucess");
			logger.info("size : " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbWorkItem> findRecordsByListAndProjectId(Integer id, List<String> accounts, Date startDate,
			Date endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE pro_id=:ID AND emp_name IN :USERS AND date BETWEEN :STARTDATE AND :ENDDATE AND status != 'CANCELLED' ORDER BY created_at DESC";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("ID", id);
			query.setParameter("USERS", accounts);
			query.setParameter("STARTDATE", startDate);
			query.setParameter("ENDDATE", endDate);
			list = query.list();
			logger.info("get TbWorkItem in List<String> sucess");
			logger.info("size : " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<Integer> findRecentItem(String account) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Integer> list = null;
		try {
			String HQL = "SELECT w.proId FROM TbWorkItem w WHERE emp_name = :USER AND status != 'CANCELLED' GROUP BY proId ORDER BY MAX(date) DESC";
			Query<Integer> query = session.createQuery(HQL, Integer.class);
			query.setParameter("USER", account);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbWorkItem> findRecordsByItems(List<Integer> ids) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE item_id IN :IDS AND status != 'CANCELLED' ORDER BY created_at DESC";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("IDS", ids);
			list = query.list();
			logger.info("get TbWorkItem in items sucess");
			logger.info("size : " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbWorkItem> findRecordsByProjects(List<Integer> ids) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbWorkItem> list = null;
		try {
			String HQL = "FROM TbWorkItem WHERE pro_id IN :IDS AND status != 'CANCELLED'";
			Query<TbWorkItem> query = session.createQuery(HQL, TbWorkItem.class);
			query.setParameter("IDS", ids);
			list = query.list();
			logger.info("get TbWorkItem in projects sucess");
			logger.info("size : " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public void save(TbWorkItem Object) throws Exception {
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
	public void delete(TbWorkItem Object) throws Exception {
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
	public TbWorkItem findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbWorkItem workItem = null;
		try {
			Transaction tx = session.beginTransaction();
			workItem = (TbWorkItem) session.get(TbWorkItem.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return workItem;
	}

}
