package com.yesee.gov.website.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import com.yesee.gov.website.dao.ProjectItemSortDao;
import com.yesee.gov.website.model.TbProjectItemSort;
import com.yesee.gov.website.util.HibernateUtil;

@Repository("projectItemSortDao")
public class ProjectItemSortDaoImpl implements ProjectItemSortDao {

	private static final Logger logger = LogManager.getLogger(ProjectItemSortDaoImpl.class);

	@Override
	public List<TbProjectItemSort> getList() throws Exception {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<TbProjectItemSort> list = null;
		String sql = null;

		try {
			sql = "FROM TbProjectItemSort";
			Query<TbProjectItemSort> query = session.createQuery(sql, TbProjectItemSort.class);
			list = query.list();
			logger.info("get project_item_sort success");
			logger.info("size = " + list.size());
		} finally {
			//session.close();
		}
		return list;
	}
 
}
