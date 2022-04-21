package employeeSystem.com.website.accounting.controller.rest;

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

import employeeSystem.com.website.accounting.service.AccountingService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = { "application/json;charset=UTF-8" })
public class RestManagerController {

	private static final Logger logger = LogManager.getLogger(RestManagerController.class);

	@Autowired
	private AccountingService accountingService;

	/**
	 * @Format JSON
	 * @Description 取得項目清單。
	 */
	@GetMapping("/manager")
	public Response getManager(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getManager(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @Format JSON
	 * @Description 取得項目下拉選單。
	 */
	@GetMapping("/itemDrop")
	public Response itemDrop(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getItemList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @Format JSON
	 * @Description 取得類別下拉選單。
	 */
	@GetMapping("/classDrop")
	public Response classDrop(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.getClassList(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @Format JSON
	 * @Description 新增項目。
	 */
	@PostMapping("/manager")
	public Response saveManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemSave(req, body);
		return Response.ok().build();
	}

	/**
	 * @Format JSON
	 * @Description 更新項目。
	 */
	@PutMapping("/manager")
	public Response updateManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemUpdate(req, body);
		return Response.ok().build();
	}

	/**
	 * @Format JSON
	 * @Description 停用項目。
	 */
	@DeleteMapping("/manager")
	public Response deleteManager(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.itemDelete(req, body);
		return Response.ok().build();
	}

	/**
	 * @Format JSON
	 * @Description 新增主項目類別。
	 */
	@PostMapping("/c/manager")
	public Response saveClass(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.classSave(req, body);
		return Response.ok().build();
	}

	/**
	 * @Format JSON
	 * @Description 更新主項目類別。
	 */
	@PutMapping("/c/manager")
	public Response updateClass(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		accountingService.classUpdate(req, body);
		return Response.ok().build();
	}

	/**
	 * @Format JSON
	 * @Description 測試用。
	 */
	@GetMapping("/example")
	public Response example(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(accountingService.example(req), MediaType.APPLICATION_JSON).build();
	}
}
