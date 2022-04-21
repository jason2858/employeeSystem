package employeeSystem.com.website.accounting.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import employeeSystem.com.website.accounting.service.ReportService;

@Controller
@RequestMapping(value = "/rest/accounting/report", produces = { "application/json;charset=UTF-8" })
public class RestReportController {

	private static final Logger logger = LogManager.getLogger(RestManagerController.class);

	@Autowired
	ReportService reportService;

	/**
	 * @Format JSON
	 * @Description 取得應付報表資料。
	 */
	@GetMapping("/receivable")
	public Response getReceivable(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(reportService.getReceivableList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @Format JSON
	 * @Description 取得應收報表資料。
	 */
	@GetMapping("/balance")
	public Response getBalance(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(reportService.getBalanceList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}

}
