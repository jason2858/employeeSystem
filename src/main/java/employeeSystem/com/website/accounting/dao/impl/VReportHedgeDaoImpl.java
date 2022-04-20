package com.yesee.gov.website.dao.accounting.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.yesee.gov.website.dao.accounting.VReportHedgeDao;
import com.yesee.gov.website.model.accounting.VTbReportHedge;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("vReportHedgeDao")
public class VReportHedgeDaoImpl extends BaseDao<VTbReportHedge> implements VReportHedgeDao {

	@Override
	public List<VTbReportHedge> getList(Map<String, Object> param) throws Exception {
		return super.findByHql(VTbReportHedge.class, param, null, null, false, 0, 0);
	}
	
	@Override
	public Map<String, Integer> getBalance(String start, String end,String iId) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		int total = 0;
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		try {
	
			String hql = "SELECT SUM(case when type = 'P' then amount else concat('-',amount) end) AS total FROM v_tb_report_hedge WHERE item ='" + iId
					+ "' AND status ='2' AND credit_date BETWEEN '"+start+"' AND '"+end+"'";

			Query query = session.createSQLQuery(hql);

			Object sum =   query.uniqueResult();
			if(StringUtils.isEmpty(sum)) {
				result.put("total", total);
			}else {
				total = (int)Double.parseDouble(sum.toString());
				result.put("total",total );
			}
		} finally {
//			session.close();
		}
		return result;
	}
}
