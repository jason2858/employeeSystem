package employeeSystem.com.website.accounting.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.accounting.service.SignCommonService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = "application/json;charset=UTF-8")
public class RestSignCommonController {

	private static final Logger logger = LogManager.getLogger(RestSignCommonController.class);

	@Autowired
	public SignCommonService signCommonService;

	/**
	 * @Format JSON
	 * @Description 取得常用簽程下拉選單。
	 */
	@GetMapping(value = "/signCommon")
	public Response getSignCommom(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		return Response.ok(signCommonService.getSignCommonList(req, resp), MediaType.APPLICATION_JSON_TYPE).build();

	}

	/**
	 * @Format JSON
	 * @Description 新增常用簽程。
	 */
	@PostMapping(value = "/signCommon")
	public Response save(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject signCommonInfo)
			throws Exception {

		return Response.ok(signCommonService.saveSignCommon(req, resp, signCommonInfo), MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	/**
	 * @Format JSON
	 * @Description 取得常用簽程內容。
	 */
	@GetMapping(value = "/signCommon/set")
	public Response getSignCommonSet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(signCommonService.getSignCommon(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}

}
