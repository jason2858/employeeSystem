package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.ProjectMemberDao;
import com.yesee.gov.website.model.TbProjectMember;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("projectMemberDao")
public class ProjectMemberDaoImpl implements ProjectMemberDao {

	private static final Logger logger = LogManager.getLogger(ProjectMemberDaoImpl.class);

	@Override
	public void save(TbProjectMember Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(Object);
			tx.commit();
			logger.info("save TbProjectMember success");
		} finally {
			//session.close();
		}

	}

	@Override
	public void delete(TbProjectMember Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
			logger.info("delete TbProjectMember success");
		} finally {
			//session.close();
		}

	}

	@Override
	public TbProjectMember findById(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbProjectMember record = null;
		try {
			Transaction tx = session.beginTransaction();
			record = (TbProjectMember) session.get(TbProjectMember.class, id);
			logger.info("find TbProjectMember id = " + id + " success.");
			tx.commit();
		} finally {
			//session.close();
		}
		return record;
	}

	@Override
	public List<TbProjectMember> getByProjectIds(List<Integer> ids) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectMember> list = null;
		try {
			String HQL = "FROM TbProjectMember WHERE project_id IN :IDs ORDER BY project_id asc,user asc";
			Query<TbProjectMember> query = session.createQuery(HQL, TbProjectMember.class);
			query.setParameter("IDs", ids);
			list = query.list();
			logger.info("find TbProjectMember In project_id success.");
			logger.info("size = " + list.size());

		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbProjectMember> getByName(String user) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectMember> list = null;
		try {
			String HQL = "FROM TbProjectMember WHERE user = :USER";
			Query<TbProjectMember> query = session.createQuery(HQL, TbProjectMember.class);
			query.setParameter("USER", user);
			list = query.list();
			logger.info("find TbProjectMember By user success.");
			logger.info("size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}
}
