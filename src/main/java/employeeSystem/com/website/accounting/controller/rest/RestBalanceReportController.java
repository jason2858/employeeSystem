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

import employeeSystem.com.website.accounting.service.BalanceReportService;

@Controller
@RequestMapping(value = "/rest/accounting/report", produces = { "application/json;charset=UTF-8" })
public class RestBalanceReportController {

	private static final Logger logger = LogManager.getLogger(RestBalanceReportController.class);

	@Autowired
	BalanceReportService balanceReportService;

	/**
	 * @Format JSON
	 * @Description 取得分類/餘額 報表資料。
	 */
	@GetMapping("itemBalance")
	public Response getItemBalance(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(balanceReportService.getItemBalanceList(req), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
