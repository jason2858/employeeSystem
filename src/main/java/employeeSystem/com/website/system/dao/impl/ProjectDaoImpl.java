package employeeSystem.com.website.system.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.system.dao.ProjectDao;
import employeeSystem.com.website.system.model.TbProject;
import employeeSystem.com.website.system.util.HibernateUtil;

@Service("projectDao")
public class ProjectDaoImpl implements ProjectDao {

	@Override
	public void save(TbProject Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(Object);
			tx.commit();
		} finally {
			// session.close();
		}

	}

	@Override
	public void delete(TbProject Object) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			session.delete(Object);
			tx.commit();
		} finally {
			// session.close();
		}

	}

	@Override
	public List<TbProject> getList(String owner) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProject> list = null;
		String hql = null;

		try {
			hql = "FROM TbProject WHERE pm = :owner";
			Query<TbProject> query = session.createQuery(hql, TbProject.class);
			query.setParameter("owner", owner);
			list = query.list();
		} finally {
			// session.close();
		}
		return list;
	}

	@Override
	public TbProject get(Integer id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		TbProject project = new TbProject();
		try {
			Transaction tx = session.beginTransaction();
			project = session.get(TbProject.class, id);
			tx.commit();
		} finally {
			// session.close();
		}
		return project;
	}

	@Override
	public List<TbProject> getAllList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProject> list = null;
		String hql = null;

		try {
			hql = "FROM TbProject ";
			list = session.createQuery(hql, TbProject.class).list();
		} finally {
			// session.close();
		}
		return list;
	}

	@Override
	public Integer getUnsignCount() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Integer count = 0;
		String hql = null;
		try {
			hql = "SELECT COUNT(*) FROM TbProject WHERE devStatus = :status";
			Query<Long> query = session.createQuery(hql, Long.class);
			query.setParameter("status", "unsign");
			count = (int) (long) query.uniqueResult();
		} finally {
			// session.close();
		}
		return count;
	}
}
