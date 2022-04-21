package employeeSystem.com.website.accounting.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounting/report")
public class ReportController {

	@GetMapping("/receivable.do")
	public String jumpReceivableJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_receivable";
	}

	@GetMapping("/balance.do")
	public String jumpBalanceJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_balance";
	}

}
