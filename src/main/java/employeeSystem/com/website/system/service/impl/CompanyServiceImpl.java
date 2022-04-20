package com.yesee.gov.website.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yesee.gov.website.dao.CompanyDao;
import com.yesee.gov.website.model.TbCompany;
import com.yesee.gov.website.service.CompanyService;

@Service("companyService")
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyDao companyDao;

	@Override
	public Map<String,String> getCompanyName() throws Exception{
		Map<String,String> map = new HashMap<>();
		List<TbCompany> list = companyDao.getList();
		for(int i = 0 ; i < list.size(); i++) {
			map.put(String.valueOf(list.get(i).getId()), list.get(i).getName());
		}
		return map;
	}

	@Override
	public List<TbCompany> getList() throws Exception {
		return companyDao.getList();
	}

}