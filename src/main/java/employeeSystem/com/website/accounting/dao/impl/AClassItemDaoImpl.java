package com.yesee.gov.website.dao.accounting.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.yesee.gov.website.dao.accounting.AClassItemDao;
import com.yesee.gov.website.model.accounting.TbAClassItem;
import com.yesee.gov.website.model.accounting.TbVoucherDetail;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("aClassItemDao")
public class AClassItemDaoImpl extends BaseDao<TbAClassItem> implements AClassItemDao {

	@Override
	public List<TbAClassItem> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(TbAClassItem.class, param, null, null, false, 0, 0);
	}

	@Override
	public TbAClassItem findById(String id) throws Exception {
		return super.findById(TbAClassItem.class, id);
	}

	@Override
	public String getAClassItemType(String item) throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();

		String type=null;
		try {
			Transaction tx = session.beginTransaction();
			StringBuffer hql = new StringBuffer();
			hql.append(
					"SELECT c.cType  FROM TbAClassItem a LEFT JOIN a.cId c on(a.cId=c.cId) WHERE a.iId ='"
							+ item + "'");

			Query query = session.createQuery(hql.toString());
			
			if(!StringUtils.isEmpty(query.uniqueResult())) {
				type =  query.uniqueResult().toString();
			}
			
		} finally {
//	session.close();
		}
		return type;
	}
}
