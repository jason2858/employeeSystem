package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.ProjectItemDao;
import com.yesee.gov.website.model.TbProjectItem;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("projectItemDao")
public class ProjectItemDaoImpl implements ProjectItemDao {

	private static final Logger logger = LogManager.getLogger(ProjectItemDaoImpl.class);

	@Override
	public TbProjectItem findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectItem> list = null;
		String sql = null;
		try {
			sql = "FROM TbProjectItem WHERE item_id = :ID";
			Query<TbProjectItem> query = session.createQuery(sql, TbProjectItem.class);
			query.setParameter("ID", id);
			list = query.list();
			logger.info("get project_item by id success");
			logger.info("size = " + list.size());
		} finally {
			//session.close();
		}
		if (list.size() != 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<TbProjectItem> getList(List<String> status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectItem> list = null;
		String sql = null;

		try {
			sql = "FROM TbProjectItem WHERE status IN :STATUS ORDER BY project_id ASC";
			Query<TbProjectItem> query = session.createQuery(sql, TbProjectItem.class);
			query.setParameter("STATUS", status);
			list = query.list();
			logger.info("get project_item success");
			logger.info("size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbProjectItem> getListByProejctId(List<Integer> ids, List<String> status) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectItem> list = null;
		String sql = null;

		try {
			sql = "FROM TbProjectItem WHERE status IN :STATUS AND project_id IN :IDS ORDER BY id ASC";
			Query<TbProjectItem> query = session.createQuery(sql, TbProjectItem.class);
			query.setParameter("IDS", ids);
			query.setParameter("STATUS", status);
			list = query.list();
			logger.info("get project_item success");
			logger.info("size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public void save(TbProjectItem object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
			logger.info("save project_item success");
		} finally {
			//session.close();
		}
	}

	@Override
	public void delete(TbProjectItem object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
			logger.info("delete project_item success");
		} finally {
			//session.close();
		}

	}

	@Override
	public Integer getUnsignCount() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbProjectItem WHERE status = :STATUS";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("STATUS", "CREATED");
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}

	@Override
	public Integer getUnsignCount(List<Integer> projectIds) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbProjectItem WHERE status = :STATUS AND project_id IN :IDS";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("STATUS", "CREATED");
			query.setParameter("IDS", projectIds);
			count = (int) (long) query.uniqueResult();
		} finally {
			//session.close();
		}
		return count;
	}
}
