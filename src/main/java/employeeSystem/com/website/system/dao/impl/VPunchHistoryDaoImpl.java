package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.VPunchHistoryDao;
import com.yesee.gov.website.model.VTbPunchHistory;
import com.yesee.gov.website.model.VTbSchedules;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("VPunchHistoryDao")
public class VPunchHistoryDaoImpl implements VPunchHistoryDao {

	@Override
	public List<VTbPunchHistory> getPunchList(String start,String end) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<VTbPunchHistory> list = null;
		try {
			String hql = "FROM VTbPunchHistory WHERE punch_date BETWEEN :start AND :end";
			Query<VTbPunchHistory> query = session.createQuery(hql, VTbPunchHistory.class);
			query.setParameter("start", start);
			query.setParameter("end", end);
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}
}
