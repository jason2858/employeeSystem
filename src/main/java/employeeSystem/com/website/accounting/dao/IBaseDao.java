package employeeSystem.com.website.accounting.dao;

import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {

	public void save(T t) throws Exception;

	public void update(T t) throws Exception;

	public T findById(Class<T> t, Object id) throws Exception;

	public void delete(T t) throws Exception;

	/**
	 * 
	 * @param t       Model實體
	 * @param param   where的參數
	 * @param groupBy group by的項目
	 * @param orderBy order by 的項目
	 * @param desc    true代表desc排序
	 * @param offset  從第幾筆起查詢
	 * @param limit   一次查幾筆
	 * @return
	 * @throws Exception
	 */
	public List<T> findByHql(Class<T> t, Map<String, Object> param, List<String> groupBy, List<String> orderBy,
			boolean desc, int offset, int limit) throws Exception;

	public List<Object[]> findBySql(List<String> column, Map<String, Object> param) throws Exception;
}