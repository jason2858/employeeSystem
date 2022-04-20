package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.FunctionDao;
import com.yesee.gov.website.model.TbFunction;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("functionDao")
public class FunctionDaoImpl implements FunctionDao {

	@Override
	public List<TbFunction> getListById(List<String> ids) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbFunction> list = null;
		String hql = null;

		try {
			hql = "FROM TbFunction WHERE id IN :ids ORDER BY sort";
			Query<TbFunction> query = session.createQuery(hql, TbFunction.class);
			query.setParameter("ids", ids);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

}
