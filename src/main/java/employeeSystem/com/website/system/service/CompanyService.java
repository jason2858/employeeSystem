package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbCompany;

public interface CompanyService {

	/**
	 * @return Map<String,String>
	 * 取得公司名稱表
	 * @throws Exception
	 */
	public Map<String,String> getCompanyName() throws Exception;
	
	/**
	 * @return List<TbCompany>
	 * 取得公司明細表
	 * @throws Exception
	 */
	public List<TbCompany> getList() throws Exception;

}
