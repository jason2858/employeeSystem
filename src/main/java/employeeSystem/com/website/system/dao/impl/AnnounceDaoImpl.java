package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.AnnounceDao;
import com.yesee.gov.website.model.TbAnnounce;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("announceDao")
public class AnnounceDaoImpl implements AnnounceDao {

	private static final Logger logger = LogManager.getLogger(AnnounceDaoImpl.class);

	@Override
	public void save(TbAnnounce Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(Object);
			tx.commit();
			logger.info("save announce success");
		} finally {
			// session.close();
		}
	}

	@Override
	public void delete(TbAnnounce Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
			logger.info("delete announce success");
		} finally {
			// session.close();
		}

	}

	@Override
	public List<TbAnnounce> getList(String type) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounce> list = null;
		String sql = null;

		try {
			sql = "FROM TbAnnounce where type like :TYPE ORDER BY id DESC";
			Query<TbAnnounce> query = session.createQuery(sql, TbAnnounce.class);
			query.setParameter("TYPE", "%" + type + "%");
			list = query.list();
			logger.info("get all announce success");
			logger.info("size = " + list.size());
		} finally {
			// session.close();
		}
		return list;
	}

	@Override
	public TbAnnounce getAnnounceById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounce> list = null;
		String sql = null;
		try {
			sql = "FROM TbAnnounce WHERE id = :ID";
			Query<TbAnnounce> query = session.createQuery(sql, TbAnnounce.class);
			query.setParameter("ID", id);
			list = query.list();
			logger.info("get announce by id success");
			logger.info("size = " + list.size());
		} finally {
			// session.close();
		}
		if (list.size() != 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<TbAnnounce> getListByCompanyId(String companyId, String type) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounce> list = null;
		String sql = null;

		try {
			sql = "FROM TbAnnounce WHERE company_id = :ID AND type = :TYPE ORDER BY id DESC";
			Query<TbAnnounce> query = session.createQuery(sql, TbAnnounce.class);
			query.setParameter("ID", companyId);
			query.setParameter("TYPE", type);
			list = query.list();
			logger.info("get announce by company_id success");
			logger.info("size = " + list.size());
		} finally {
			// session.close();
		}
		return list;
	}

	@Override
	public List<TbAnnounce> getListMoreThanIdAndCompanyId(Integer id, String companyId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounce> list = null;
		String sql = null;

		try {
			sql = "FROM TbAnnounce WHERE id > :ID AND company_id = :COMPANYID ORDER BY id DESC";
			Query<TbAnnounce> query = session.createQuery(sql, TbAnnounce.class);
			query.setParameter("ID", id);
			query.setParameter("COMPANYID", companyId);
			list = query.list();
			logger.info("get unread announce success");
			logger.info("size = " + list.size());
		} finally {
			// session.close();
		}
		return list;
	}

}
