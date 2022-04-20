package com.yesee.gov.website.service;

import java.util.Map;


public interface DirectRejectService {
	
	/**
	 * @param code
	 * @param reason
	 * @return Map<String, Object>
	 * 依照駁回編碼取得駁回結果物件
	 * @throws Exception
	 */
	public Map<String, Object> getDirectRejectMap(String code, String reason) throws Exception;

}