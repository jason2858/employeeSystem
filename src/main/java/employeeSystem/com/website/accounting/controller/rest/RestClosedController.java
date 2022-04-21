package employeeSystem.com.website.accounting.controller.rest;

import java.awt.PageAttributes.MediaType;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.accounting.service.ClosedService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = { "application/json;charset=UTF-8" })
public class RestClosedController {

	private static final Logger logger = LogManager.getLogger(RestManagerController.class);

	@Autowired
	private ClosedService closedService;

	@GetMapping("/closed")
	public Response getClosed(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(closedService.getClosed(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PutMapping("/closed")
	public Response updateClosed(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		closedService.updateClosed(req, body);
		return Response.ok().build();
	}

}
