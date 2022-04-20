package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yesee.gov.website.dao.AnnounceDocDao;
import com.yesee.gov.website.model.TbAnnounceDoc;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("announceDocDao")
public class AnnounceDocDaoImpl implements AnnounceDocDao {

	private static final Logger logger = LogManager.getLogger(AnnounceDocDaoImpl.class);

	@Override
	public void save(TbAnnounceDoc object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
			logger.info("save doc success");
		} finally {
			// session.close();
		}

	}

	@Transactional
	@Override
	public void deleteDocByAnnounceId(Integer AnnounceId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
//		String sql = null;
		logger.info("AnnounceId  " + AnnounceId);
		try {
			Transaction tx = session.beginTransaction();
//			sql = "DELETE FROM TbAnnounceDoc WHERE announceId = :ID ";
//			Query<TbAnnounceDoc> query = session.createQuery(sql, TbAnnounceDoc.class);
			Query<TbAnnounceDoc> query = session.createQuery("DELETE FROM TbAnnounceDoc WHERE announceId = :ID ");
			query.setParameter("ID", String.valueOf(AnnounceId));
			query.executeUpdate();
			tx.commit();
			logger.info("deleteDocbyAnnounceId doc success");
		} finally {
			// session.close();
		}
	}

	@Override
	public List<TbAnnounceDoc> getRecordsByAnnounceId(Integer AnnounceId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounceDoc> list = null;
		String sql = null;

		try {
			sql = "FROM TbAnnounceDoc WHERE announceId = :ID ";
			Query<TbAnnounceDoc> query = session.createQuery(sql, TbAnnounceDoc.class);
			query.setParameter("ID", String.valueOf(AnnounceId));
			list = query.list();
			logger.info("getRecordsByAnnounceId doc success");
		} finally {
			// session.close();
		}
		return list;
	}

	@Override
	public void deleteByDocId(TbAnnounceDoc object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
			logger.info("delete Doc success");
		} finally {
			// session.close();
		}

	}

	@Override
	public TbAnnounceDoc getDocById(Integer docId) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAnnounceDoc> list = null;
		String sql = null;

		try {
			sql = "FROM TbAnnounceDoc WHERE docId = :ID ";
			Query<TbAnnounceDoc> query = session.createQuery(sql, TbAnnounceDoc.class);
			query.setParameter("ID", docId);
			list = query.list();
			logger.info("getDocById success");
		} finally {
			// session.close();
		}
		if (list.size() != 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
