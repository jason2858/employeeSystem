package com.yesee.gov.website.service;

import com.yesee.gov.website.model.TbSchedules;

public interface SignService {
	
	/**
	 * @param record
	 * 簽核過後寄送郵件
	 * @throws Exception
	 */
	public void sendRespondEmail(TbSchedules record) throws Exception;

}