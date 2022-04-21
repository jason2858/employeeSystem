package employeeSystem.com.website.system.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import employeeSystem.com.website.system.model.TbConfig;

public class Config {

	private static final Logger logger = LogManager.getLogger(Config.class);

	private static Config config = null;

	private static Map<String, Object> map = new HashMap<String, Object>();

	public static Config getInstance() {
		if (config == null) {
			config = new Config();
			config.init();
		}

		return config;
	}

	public static void reload() {
		config = new Config();
		config.init();
	}

	private void init() {
		logger.info("Config reload");
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			String HQL = "from TbConfig";
			List<?> list = session.createQuery(HQL).list();
			// session.close();
			Iterator<?> it = list.iterator();
			Map<String, Object> map = new HashMap<String, Object>();
			while (it.hasNext()) {
				TbConfig record = (TbConfig) it.next();
				map.put(record.getKey(), record);
			}
			Config.map = map;
		} finally {
			// session.close();
		}
	}

	public String getValue(String key) {
		TbConfig config = (TbConfig) Config.map.get(key);
		return config.getValue();
	}

	public TbConfig getObject(String key) {
		return (TbConfig) Config.map.get(key);
	}

}