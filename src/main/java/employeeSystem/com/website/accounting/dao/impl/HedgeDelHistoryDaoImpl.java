package employeeSystem.com.website.accounting.dao.impl;

import org.springframework.stereotype.Repository;

import employeeSystem.com.website.accounting.dao.HedgeDelHistoryDao;
import employeeSystem.com.website.accounting.model.TbHedgeDelHistory;

@Repository("HedgeDelHistoryDao")
public class HedgeDelHistoryDaoImpl extends BaseDao<TbHedgeDelHistory> implements HedgeDelHistoryDao {

}
