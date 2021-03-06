package employeeSystem.com.website.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import employeeSystem.com.website.system.dao.PreferenceDao;
import employeeSystem.com.website.system.model.TbPreference;
import employeeSystem.com.website.system.service.PreferenceService;

@Service("preferenceService")
public class PreferenceServiceImpl implements PreferenceService {

	@Autowired
	private PreferenceDao preferenceDao;

	@Override
	public void save(TbPreference object) throws Exception {
		preferenceDao.save(object);
	}

	@Override
	public void del(TbPreference object) throws Exception {
		preferenceDao.delete(object);
	}

	@Override
	public TbPreference getByUserAndKey(String user, String key) throws Exception {
		return preferenceDao.getByUserAndKey(user, key);
	}

	@Override
	public List<TbPreference> getByUser(String user) throws Exception {
		return preferenceDao.getByUser(user);
	}
}