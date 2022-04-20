package com.yesee.gov.website.service;

import java.util.Map;


public interface DirectSignService {
	
	/**
	 * @param code
	 * @return Map<String, Object>
	 * 依照簽核編碼取得簽核結果物件
	 * @throws Exception
	 */
	public Map<String, Object> getDirectSignMap(String code) throws Exception;

}