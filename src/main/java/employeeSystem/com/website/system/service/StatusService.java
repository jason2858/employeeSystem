package com.yesee.gov.website.service;

import java.util.List;
import java.util.Map;

import com.yesee.gov.website.model.TbEmployees;

public interface StatusService {

	/**
	 * @param nameSelect
	 * @param authorise
	 * @param depId
	 * @param entity
	 * @return List<Map<String, Object>>
	 * 取得員工當日上下班狀態
	 * @throws Exception
	 */
	public List<Map<String, Object>> getRecords(String nameSelect, String authorise, String depId, TbEmployees entity) throws Exception;

}
