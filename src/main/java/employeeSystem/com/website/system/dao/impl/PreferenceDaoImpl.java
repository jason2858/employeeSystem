package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.PreferenceDao;
import com.yesee.gov.website.model.TbPreference;
import com.yesee.gov.website.util.HibernateUtil;

@Service("PreferenceDao")
public class PreferenceDaoImpl implements PreferenceDao {

	private static final Logger logger = LogManager.getLogger(PreferenceDao.class);

	@Override
	public void save(TbPreference object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
			logger.info("save TbPreference success");
		} finally {
			//session.close();
		}

	}

	@Override
	public void delete(TbPreference object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
			logger.info("del TbPreference success");
		} finally {
			//session.close();
		}

	}

	@Override
	public TbPreference getByUserAndKey(String user, String key) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPreference> list = null;
		try {
			String HQL = "FROM TbPreference WHERE username = :USER AND config_key = :KEY";
			Query<TbPreference> query = session.createQuery(HQL, TbPreference.class);
			query.setParameter("USER", user);
			query.setParameter("KEY", key);
			list = query.list();
		} finally {
			//session.close();
		}
		if (list.size() != 0) {
			logger.info("find TbPreference by user and key success");
			logger.info("user :" + list.get(0).getId().getUsername());
			logger.info("key :" + list.get(0).getId().getConfigKey());
			logger.info("value :" + list.get(0).getValue());
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<TbPreference> getByUser(String user) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbPreference> list = null;
		try {
			String HQL = "FROM TbPreference WHERE username = :USER";
			Query<TbPreference> query = session.createQuery(HQL, TbPreference.class);
			query.setParameter("USER", user);
			list = query.list();
		} finally {
			//session.close();
		}
		if (list.size() != 0) {
			return list;
		} else {
			return null;
		}
	}

}
