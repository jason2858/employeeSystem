package employeeSystem.com.website.accounting.controller.rest;

import java.awt.PageAttributes.MediaType;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.accounting.service.AccountingService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = { "application/json;charset=UTF-8" })
public class RestManagerController {

	private static final Logger logger = LogManager.getLogger(RestManagerController.class);

	@Autowired
	private AccountingService accountingService;

	/**
	 * @param c_id, i_id, i_name
	 * @return classList: {cName, itemList: {iId, iName, directions}}
	 * @description 取得項目管理清單。
	 * @description 透過aAccountingService.getManager取得項目管理清單並回傳至前端。
	 */
	@GetMapping("/manager")
	public Response getManager(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getManager(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param c_id, "c_type"
	 * @return itemList: {iId, iName}
	 * @description 取得項目下拉選單。
	 */
	@GetMapping("/itemDrop")
	public Response itemDrop(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getItemList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param
	 * @return classList: {cId, cName}
	 * @description 取得類別下拉選單。
	 */
	@GetMapping("/classDrop")
	public Response classDrop(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getClassList(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/manager")
	public Response saveManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemSave(req, body);
		return Response.ok().build();
	}

	@PutMapping("/manager")
	public Response updateManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemUpdate(req, body);
		return Response.ok().build();
	}

	@DeleteMapping("/manager")
	public Response deleteManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemDelete(req, body);
		return Response.ok().build();
	}

	@PostMapping("/c/manager")
	public Response saveClass(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.classSave(req, body);
		return Response.ok().build();
	}

	@PutMapping("/c/manager")
	public Response updateClass(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.classUpdate(req, body);
		return Response.ok().build();
	}

	@GetMapping("/example")
	public Response example(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.example(req), MediaType.APPLICATION_JSON).build();
	}
}
