package com.yesee.gov.website.controller.accounting.rest;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yesee.gov.website.pojo.accounting.InsertVoucherInfo;
import com.yesee.gov.website.pojo.accounting.UpdateVoucherInfo;
import com.yesee.gov.website.service.accounting.VoucherService;

import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/voucher", produces = "application/json;charset=UTF-8")
public class RestVoucherController {

	private static final Logger logger = LogManager.getLogger(RestVoucherController.class);

	@Autowired
	public VoucherService voucherService;

	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得常用傳票清單。
	 *                   透過voucherCommonDao.getVoucherCommonList取得常用傳票清單資料並回傳至前端。
	 */
	@GetMapping(value = "/common")
	public Response getCommom(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(voucherService.getVoucherCommonList(req, resp), MediaType.APPLICATION_JSON_TYPE).build();

	}

	/**
	 * @RequestBody InsertVoucherInfo
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 儲存傳票。接收前端傳回的voucherInfo
	 *                   透過voucherService.save儲存資料
	 */
	@PostMapping(value = "/api")
	public Response save(HttpServletRequest req, HttpServletResponse resp, @RequestBody InsertVoucherInfo voucherInfo)
			throws Exception {

		return Response.ok(voucherService.saveVoucher(req, resp, voucherInfo), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @RequestBody InsertVoucherInfo
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 儲存傳票。接收前端傳回的voucherInfo
	 *                   透過voucherService.update儲存資料
	 */
	@PutMapping(value = "/api")
	public Response update(HttpServletRequest req, HttpServletResponse resp, @RequestBody UpdateVoucherInfo voucherInfo)
			throws Exception {

		return Response.ok(voucherService.updateVoucher(req, resp, voucherInfo), MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	/**
	 * @RequestBody InsertVoucherInfo
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 傳票送出。 透過voucherService.checkVoucherSignAndSend送出資料。
	 */
	@PostMapping(value = "/send")
	public Response send(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject voucherInfo) throws Exception {

		return Response.ok(voucherService.sendVoucher(req, resp,voucherInfo), MediaType.APPLICATION_JSON_TYPE).build();

	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得傳票資料。 透過voucherService取得資料並回傳至前端。
	 */
	@GetMapping(value = "/api")
	public Response getVoucher(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		return Response.ok(voucherService.getVoucher(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得傳票表頭。 透過voucherService取得資料並回傳至前端。
	 */
	@GetMapping(value = "/api/h")
	public Response getVoucherHead(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		return Response.ok(voucherService.getVoucherHead(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得傳票明細。 透過voucherService取得資料並回傳至前端。
	 */
	@GetMapping(value = "/api/d")
	public Response getVoucherDetail(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		return Response.ok(voucherService.getVoucherDetail(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得傳票修改權限。 透過voucherService取得資料並回傳至前端。
	 */
	@GetMapping(value = "/getVoucherMToken")
	public Response getVoucherTokenHead(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		return Response.ok(voucherService.getVoucherMToken(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 刪除傳票明細
	 */
	@DeleteMapping(value = "/api")
	public Response deleteVoucher (HttpServletRequest req, HttpServletResponse resp,@RequestBody JSONObject voucherInfo) throws Exception{
		return Response.ok(voucherService.deleteVoucher(req, resp, voucherInfo), MediaType.APPLICATION_JSON_TYPE).build();
	}

}
