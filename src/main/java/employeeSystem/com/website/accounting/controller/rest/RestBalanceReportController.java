package employeeSystem.com.website.accounting.controller.rest;

import java.awt.PageAttributes.MediaType;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import employeeSystem.com.website.accounting.service.BalanceReportService;

@Controller
@RequestMapping(value = "/rest/accounting/report", produces = { "application/json;charset=UTF-8" })
public class RestBalanceReportController {

	private static final Logger logger = LogManager.getLogger(RestBalanceReportController.class);

	@Autowired
	BalanceReportService balanceReportService;

	@GetMapping("itemBalance")
	public Response getItemBalance(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(balanceReportService.getItemBalanceList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
