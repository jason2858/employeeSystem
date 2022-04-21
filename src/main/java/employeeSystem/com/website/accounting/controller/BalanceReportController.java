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
public class BalanceReportController {

	/**
	 * @Format Web View
	 * @Description 取得餘額表頁面
	 */
	@GetMapping("/hedgeBalance.do")
	public String jumpHedgeBalanceJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_hedge_balance";
	}

	/**
	 * @Format Web View
	 * @Description 取得分類帳頁面
	 */
	@GetMapping("/itemBalance.do")
	public String jumpItemBalanceJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_item_balance";
	}
}
