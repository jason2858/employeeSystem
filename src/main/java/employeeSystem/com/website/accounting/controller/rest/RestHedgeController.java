package employeeSystem.com.website.accounting.controller.rest;

import java.awt.PageAttributes.MediaType;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.accounting.service.HedgeService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = { "application/json;charset=UTF-8" })
public class RestHedgeController {

	private static final Logger logger = LogManager.getLogger(RestHedgeController.class);

	@Autowired
	private HedgeService hedgeService;

	@GetMapping("/hedge")
	public Response getHedge(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(hedgeService.getHedge(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping("/hedge")
	public Response saveVoucherHedge(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		return Response.ok(hedgeService.saveHedge(req, body), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
