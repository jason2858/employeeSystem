package com.yesee.gov.website.service;

public interface SendMailService {

	/**
	 * @param recipient
	 * @param subject
	 * @param text
	 * 寄送郵件
	 * @throws Exception
	 */
	public void sendEmail(String recipient,String subject,String text) throws Exception;

}