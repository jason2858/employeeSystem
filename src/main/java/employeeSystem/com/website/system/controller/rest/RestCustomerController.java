package com.yesee.gov.website.controller.rest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yesee.gov.website.model.TbCustomer;
import com.yesee.gov.website.service.CustomerService;

@RestController
@RequestMapping(value = "/rest/customer", produces = { "application/json;charset=UTF-8" })
public class RestCustomerController {
	private static final Logger logger = LogManager.getLogger(RestCustomerController.class);
	
	@Autowired
	private CustomerService customerService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得可見客戶資訊。
	 * 透過customerService.getList取得可見客戶資料並回傳至前端。
	 */
	@PostMapping("/getList")
	public String getCustomerList(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String jsonMsg = null;

		String account = req.getSession().getAttribute("Account").toString();
		String authorise = req.getSession().getAttribute("Authorise").toString();

		try {
			List<TbCustomer> pList = customerService.getList(account, authorise);
			if (!CollectionUtils.isEmpty(pList)) {
				ObjectMapper mapper = new ObjectMapper();
				jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pList);
			} 
		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param vo
	 * @throws ServletException
	 * @throws IOException
	 * 儲存客戶資料。
	 * 接收前端傳回的TbCustomer vo並透過customerService.save儲存。
	 */
	@PostMapping("/save")
	public void saveCustomer(HttpServletRequest req, HttpServletResponse resp, @RequestBody TbCustomer vo)
			throws ServletException, IOException {
		String account = req.getSession().getAttribute("Account").toString();
		try {
			logger.info("saveCustomer name:"+vo.getName());
			logger.info("type:"+vo.getType());
			logger.info("info:"+vo.getInfo());
			logger.info("ein:"+vo.getEin());
			logger.info("contactPerson:"+vo.getContactPerson());
			logger.info("contactPhone:"+vo.getContactPhone());
			logger.info("contactEmail:"+vo.getContactEmail());
			customerService.save(vo, account);
			logger.info("saveCustomer Success");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param id
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 取得特定客戶資訊。
	 * 透過customerService.findById取得特定客戶資料並回傳至前端。
	 */
	@PostMapping("/get")
	public String getCustomer(HttpServletRequest req, HttpServletResponse resp,
			@RequestParam(value = "id", required = false) Integer id) throws ServletException, IOException {
		String jsonMsg = null;
		try {
			TbCustomer c = customerService.findById(id);
			ObjectMapper mapper = new ObjectMapper();
			jsonMsg = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(c);

		} catch (Exception e) {
			logger.error(e);
		}
		return jsonMsg;
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param vo
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * 更新客戶資料。
	 * 接收前端傳回的TbCustomer vo並透過customerService.update儲存。
	 * 回傳更新結果至前端(success/false)。
	 */
	@PostMapping("/update")
	public Response updateCustomer(HttpServletRequest req, HttpServletResponse resp, @RequestBody TbCustomer vo)
			throws ServletException, IOException {
		try {
			TbCustomer object = customerService.checkUpdate(vo);
			if(object != null) {
				logger.info("updateCustomer name:"+vo.getName());
				logger.info("id:"+vo.getId());
				logger.info("type:"+vo.getType());
				logger.info("info:"+vo.getInfo());
				logger.info("ein:"+vo.getEin());
				logger.info("contactPerson:"+vo.getContactPerson());
				logger.info("contactPhone:"+vo.getContactPhone());
				logger.info("contactEmail:"+vo.getContactEmail());
				customerService.update(vo, object);
				logger.info("updateCustomer Success");
			}else {
				return Response.ok("該客戶資料已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("修改成功", MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param vo
	 * @throws ServletException
	 * @throws IOException
	 * 刪除客戶資料。
	 * 接收前端傳回的TbCustomer vo並透過ccustomerService.delete刪除。
	 */
	@PostMapping("/delete")
	public Response deleteCustomer(HttpServletRequest req, HttpServletResponse resp, @RequestBody TbCustomer vo)
			throws ServletException, IOException {

		try {
			TbCustomer object = customerService.checkUpdate(vo);
			if(object != null) {
				logger.info("deleteCustomer id:"+ vo.getId());
				customerService.delete(object);
				logger.info("deleteCustomer Success");
			}else {
				return Response.ok("該客戶資料已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("刪除成功", MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * @param req
	 * @param resp
	 * @param vo
	 * @throws ServletException
	 * @throws IOException
	 * 簽核客戶資料。
	 * 接收前端傳回的TbCustomer vo並透過customerService.sign簽核。
	 */
	@PostMapping("/sign")
	public Response sign(HttpServletRequest req, HttpServletResponse resp, @RequestBody TbCustomer vo)
			throws ServletException, IOException {
		
		try {
			TbCustomer object = customerService.checkUpdate(vo);
			if(object != null) {
				logger.info("signCustomer id:"+ vo.getId());
				customerService.sign(object);
				logger.info("signCustomer Success");
			}else {
				return Response.ok("該客戶資料已被更新", MediaType.APPLICATION_JSON_TYPE).build();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return Response.ok("簽核成功", MediaType.APPLICATION_JSON_TYPE).build();
	}

}