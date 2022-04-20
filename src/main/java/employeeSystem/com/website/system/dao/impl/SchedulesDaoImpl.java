package com.yesee.gov.website.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.SchedulesDao;
import com.yesee.gov.website.model.TbSchedules;
import com.yesee.gov.website.model.VTbSchedules;
import com.yesee.gov.website.util.DateUtil;
import com.yesee.gov.website.util.HibernateUtil;

@Service("schedulesDao")
public class SchedulesDaoImpl implements SchedulesDao {

	private static final Logger logger = LogManager.getLogger(SchedulesDao.class);

	@Override
	public List<TbSchedules> getSchedulesByEmployees(String employees) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		String hql = null;

		try {
			hql = "FROM TbSchedules WHERE user = :employees";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("employees", employees);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> getSchedulesByEmployees(List<String> employees) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		String hql = null;

		try {
			hql = "FROM TbSchedules WHERE user IN :employees";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("employees", employees);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		String hql = null;

		try {
			hql = "FROM TbSchedules ";
			list = session.createQuery(hql, TbSchedules.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public TbSchedules findById(int id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbSchedules schedule = null;
		try {
			Transaction tx = session.beginTransaction();
			schedule = (TbSchedules) session.get(TbSchedules.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return schedule;
	}

	@Override
	public List<TbSchedules> findScheduleByStatus(List<String> nameList, String status, String startDate,
			String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String HQL = "FROM TbSchedules WHERE user IN (:USERS) AND status IN (:STATUS) AND start_time BETWEEN :STARTDATE AND :ENDDATE";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
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
	public Integer getUnsignCount(List<String> users) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbSchedules WHERE user IN :users AND  (status = 'CREATED' OR status = 'MODIFIED' OR status = 'WAIT_DELETE')";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("users", users);
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}

	@Override
	// public List<TbSchedules> findAllScheduleByStatus(List<String> nameList,
	// List<String> status) throws Exception {
	public List<TbSchedules> findAllScheduleByStatus(List<String> nameList, String status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			// String HQL = "FROM TbSchedules WHERE user IN (:USERS) AND status IN :STATUS";
			String HQL = "FROM TbSchedules WHERE user IN (:USERS) AND status = :STATUS";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
			query.setParameter("USERS", nameList);
			query.setParameter("STATUS", status);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public TbSchedules getById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbSchedules schedules = new TbSchedules();
		try {
			Transaction tx = session.beginTransaction();
			schedules = session.get(TbSchedules.class, id);
			tx.commit();
		} finally {
			//session.close();
		}
		return schedules;
	}

	@Override
	public void save(TbSchedules Object) throws Exception {
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
	// public List<TbSchedules> getSchedulesInfo(int authorise, String account,
	// String startTime, String endTime,List<String> statusFilter)
	public List<TbSchedules> getSchedulesInfo(int authorise, String account, String startTime, String endTime)
			throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			// String HQL = "FROM TbSchedules WHERE status NOT IN :statusFilter AND user =
			// :user AND ( start_time BETWEEN :startTime AND :endTime ) AND ( end_time
			// BETWEEN :startTime AND :endTime )";
			String HQL = "FROM TbSchedules WHERE status != :status AND user = :user AND ( start_time BETWEEN :startTime AND :endTime ) AND ( end_time BETWEEN :startTime AND :endTime )";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
			query.setParameter("user", account);
			query.setParameter("startTime", DateUtil.StringToDate(startTime));
			query.setParameter("endTime", DateUtil.StringToDate(endTime));
			query.setParameter("status", "CANCELLED");
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public void delete(TbSchedules Object) throws Exception {
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
	public List<TbSchedules> findByEmployeesAndType(String empName, Integer type) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql = null;
		List<TbSchedules> list = null;
		try {
			hql = "FROM TbSchedules WHERE type = :type AND user = :user ";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("user", empName);
			query.setParameter("type", type);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public int checkSignedOrRejected(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		try {
			String hql = "SELECT COUNT(*) FROM TbSchedules WHERE id =:id AND (status!='SIGNED' OR status!='REJECTED')";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("id", id);
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}

	@Override
	public List<TbSchedules> getAnnualLeaveInfo(String account) throws Exception {

		Session session = HibernateUtil.getSessionFactory().openSession();
		String hql = null;
		List<TbSchedules> list = null;
		try {
			hql = "FROM TbSchedules WHERE type = 3 AND user = :user AND status = 'SIGNED' ";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("user", account);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> findAllScheduleByStatuses(String name, List<String> statusArray, String startDate,
			String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String HQL = "FROM TbSchedules WHERE user = :USERS AND status IN (:STATUS) AND start_time BETWEEN :STARTDATE AND :ENDDATE";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
			query.setParameter("USERS", name);
			query.setParameter("STATUS", statusArray);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> findAllScheduleByType(String name, String type, List<String> statusArray, String startDate,
			String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String HQL = "FROM TbSchedules WHERE user = :USERS AND type = :TYPE AND status IN (:STATUS) AND start_time BETWEEN :STARTDATE AND :ENDDATE";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
			query.setParameter("USERS", name);
			query.setParameter("TYPE", Integer.parseInt(type));
			query.setParameter("STATUS", statusArray);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> findScheduleByDate(String user, String status, String startDate, String endDate)
			throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String HQL = "FROM TbSchedules WHERE user = :USER AND status = :STATUS AND (start_time BETWEEN :STARTDATE AND :ENDDATE OR end_time BETWEEN :STARTDATE AND :ENDDATE)";
			Query<TbSchedules> query = session.createQuery(HQL, TbSchedules.class);
			query.setParameter("USER", user);
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
	public List<TbSchedules> findSchedulesOfYear(String account, Date year) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String hql = "FROM TbSchedules WHERE (YEAR(startTime)= YEAR(:year) OR YEAR(endTime)= YEAR(:year)) AND user = :USER AND status = 'SIGNED'";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("USER", account);
			query.setParameter("year", year);
			list = query.list();
			logger.info("get schedules of years success size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbSchedules> findTravelByCreatedAt(Date createdAt) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbSchedules> list = null;
		try {
			String hql = "FROM TbSchedules WHERE DATE(created_at) = :TIME AND form_no != NULL ORDER BY form_no DESC";
			Query<TbSchedules> query = session.createQuery(hql, TbSchedules.class);
			query.setParameter("TIME", createdAt);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<VTbSchedules> findSchedulesOfAvailableTime(String account) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<VTbSchedules> list = null;
		try {
			String hql = "FROM VTbSchedules WHERE user = :USER";
			Query<VTbSchedules> query = session.createQuery(hql, VTbSchedules.class);
			query.setParameter("USER", account);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}
}
