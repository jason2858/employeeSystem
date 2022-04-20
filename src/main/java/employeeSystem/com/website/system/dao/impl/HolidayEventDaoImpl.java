package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.yesee.gov.website.dao.HolidayEventDao;
import com.yesee.gov.website.model.TbHolidayEvent;
import com.yesee.gov.website.util.DateUtil;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("holidayEventDao")
public class HolidayEventDaoImpl implements HolidayEventDao {

	@Override
	public void save(String holidayName, String startDate, String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbHolidayEvent> list = null;
		try {
			Transaction tx = session.beginTransaction();
			TbHolidayEvent holiday = new TbHolidayEvent();
			holiday.setHolidayName(holidayName);
			holiday.setStartDate(DateUtil.StringToDate(startDate));
			holiday.setHolidayType(1);

			list = this.get(holidayName, startDate);
			if (CollectionUtils.isEmpty(list)) {
				session.saveOrUpdate(holiday);
				tx.commit();
			}
		} finally {
			//session.close();
		}

	}

	@Override
	public List<TbHolidayEvent> get(String holidayName, String startDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbHolidayEvent> list = null;
		String hql = null;

		try {
			hql = "FROM TbHolidayEvent WHERE holiday_name = :holidayName AND start_date = :startDate ";
			Query<TbHolidayEvent> query = session.createQuery(hql, TbHolidayEvent.class);
			query.setParameter("holidayName", holidayName);
			query.setParameter("startDate", DateUtil.StringToDate(startDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbHolidayEvent> getAll() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbHolidayEvent> list = null;
		String sql = null;

		try {
			sql = "FROM TbHolidayEvent ";
			list = session.createQuery(sql, TbHolidayEvent.class).list();
		} finally {
			//session.close();
		}
		return list;
	}

	@Override
	public List<TbHolidayEvent> findHolidayByDates(String startDate, String endDate) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbHolidayEvent> list = null;
		try {
			String HQL = "FROM TbHolidayEvent WHERE start_date BETWEEN :STARTDATE AND :ENDDATE";
			Query<TbHolidayEvent> query = session.createQuery(HQL, TbHolidayEvent.class);
			query.setParameter("STARTDATE", DateUtil.StringToDate(startDate));
			query.setParameter("ENDDATE", DateUtil.StringToDate(endDate));
			list = query.list();
		} finally {
			//session.close();
		}
		return list;
	}
}