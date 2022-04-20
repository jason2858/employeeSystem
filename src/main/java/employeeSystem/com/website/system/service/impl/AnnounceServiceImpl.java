package com.yesee.gov.website.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.AnnounceDao;
import com.yesee.gov.website.dao.AnnounceDocDao;
import com.yesee.gov.website.model.TbAnnounce;
import com.yesee.gov.website.model.TbAnnounceDoc;
import com.yesee.gov.website.service.AnnounceService;

@Service("announceService")
public class AnnounceServiceImpl implements AnnounceService {

	@Autowired
	private AnnounceDocDao announceDocDao;

	@Autowired
	private AnnounceDao announceDao;

	@Override
	public List<TbAnnounce> getRecords(Integer authorise, String companyId, String type) throws Exception {
		if (authorise == 1) {
			return announceDao.getList(type);
		} else {
			return announceDao.getListByCompanyId(companyId, type);
		}
	}

	@Override
	public List<TbAnnounceDoc> getRecordsByAnnounceId(Integer AnnounceId) throws Exception {
		return announceDocDao.getRecordsByAnnounceId(AnnounceId);
	}

	@Override
	public void update(TbAnnounce object) throws Exception {
		announceDao.save(object);
	}

	@Override
	public TbAnnounce getAnnounceById(Integer id) throws Exception {
		return announceDao.getAnnounceById(id);
	}

	@Override
	public void updateDoc(TbAnnounceDoc object) throws Exception {
		announceDocDao.save(object);
	}

	@Override
	public void delete(Integer id) throws Exception {
		TbAnnounce record = announceDao.getAnnounceById(id);
		announceDocDao.deleteDocByAnnounceId(id);
		announceDao.delete(record);
	}

	@Override
	public List<TbAnnounce> getUnreadAnnounce(Integer id, String companyId) throws Exception {
		return announceDao.getListMoreThanIdAndCompanyId(id, companyId);
	}

	@Override
	public void deleteDoc(Integer docId) throws Exception {
		TbAnnounceDoc record = announceDocDao.getDocById(docId);
		announceDocDao.deleteByDocId(record);
	}

	@Override
	public String getFilePath(Integer docId) throws Exception {
		TbAnnounceDoc record = announceDocDao.getDocById(docId);
		String delDocPath = record.getDocPath();
		return delDocPath;
	}
}