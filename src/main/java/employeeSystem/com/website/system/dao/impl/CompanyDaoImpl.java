package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.CompanyDao;
import com.yesee.gov.website.model.TbCompany;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("companyDao")
public class CompanyDaoImpl implements CompanyDao {

	private static final Logger logger = LogManager.getLogger(CompanyDaoImpl.class);

	@Override
	public void save(TbCompany Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(Object);
			tx.commit();
			logger.info("save TbCompany success");
		} finally {
			//session.close();
		}

	}

	@Override
	public void delete(TbCompany Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
			logger.info("delete TbCompany success");
		} finally {
			//session.close();
		}

	}

	@Override
	public List<TbCompany> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbCompany> list = null;
		String sql = null;

		try {
			sql = "FROM TbCompany ";
			list = session.createQuery(sql, TbCompany.class).list();
			logger.info("get TbCompany sucess");
			logger.info("size : " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}

}
