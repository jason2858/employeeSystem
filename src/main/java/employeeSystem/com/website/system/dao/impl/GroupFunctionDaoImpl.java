package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.GroupFunctionDao;
import com.yesee.gov.website.model.TbGroupFunction;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("groupFunctionDao")
public class GroupFunctionDaoImpl implements GroupFunctionDao {

	@Override
	public List<TbGroupFunction> getListById(String id) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbGroupFunction> list = null;
		String hql = null;

		try {
			hql = "FROM TbGroupFunction gf WHERE gf.id.groupId = :id order by gf.id.funcId";
			Query<TbGroupFunction> query = session.createQuery(hql, TbGroupFunction.class);
			query.setParameter("id", id);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

}
