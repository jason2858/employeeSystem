package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yesee.gov.website.dao.accounting.AccountingBalanceDao;
import com.yesee.gov.website.model.accounting.TbAccountingBalance;
import com.yesee.gov.website.model.accounting.TbAccountingClass;
import com.yesee.gov.website.model.accounting.TbVoucherHead;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("accountingBalanceDao")
public class AccountingBalanceDaoImpl extends BaseDao<TbAccountingBalance> implements AccountingBalanceDao {
	
	@Override
	public List<TbAccountingBalance> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbAccountingBalance.class, param, null, null, false, 0, 0);
	}
	
	@Override
	public List<TbAccountingBalance> getBList(String month ,String year) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAccountingBalance> list = null;
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append("FROM TbAccountingBalance WHERE month =:month AND year =:year");
			Query<TbAccountingBalance> query = session.createQuery(hql.toString(), TbAccountingBalance.class);
			query.setParameter("month", Integer.parseInt(month));
			query.setParameter("year", Integer.parseInt(year));
			list = query.list();
		} finally {
//			session.close();
		}

		return list;
	}
	
	@Override
	public List<TbAccountingBalance> getBOne(String month ,String year,String item) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbAccountingBalance> list = null;
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append("FROM TbAccountingBalance WHERE month =:month AND year =:year AND i_id =:item");
			Query<TbAccountingBalance> query = session.createQuery(hql.toString(), TbAccountingBalance.class);
			query.setParameter("month", Integer.parseInt(month));
			query.setParameter("year", Integer.parseInt(year));
			query.setParameter("item", Integer.parseInt(item));
			list = query.list();
		} finally {
//			session.close();
		}

		return list;
	}
	
	

}
