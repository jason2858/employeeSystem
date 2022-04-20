package com.yesee.gov.website.service.accounting;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;

import com.yesee.gov.website.exception.AccountingException;

import net.sf.json.JSONObject;

public interface AccountingService {

	public String getManager(HttpServletRequest req) throws AccountingException, Exception;

	public String getItemList(HttpServletRequest req) throws AccountingException, Exception;

	public String getClassList() throws AccountingException, Exception;

	/**
	 * @description 新稱項目管理清單。
	 * @description TbAClassItem - tb_a_class_item
	 */
	public void itemSave(HttpServletRequest req, JSONObject body) throws AccountingException, Exception;

	public void itemUpdate(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception;

	public void itemDelete(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception;

	/**
	 * @description 新稱類別清單。
	 * @description TbAccountingClass - tb_accounting_class
	 */
	public void classSave(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception;

	public void classUpdate(HttpServletRequest req, @RequestBody JSONObject body) throws AccountingException, Exception;

	public String example(HttpServletRequest req) throws AccountingException, Exception;
}
