package employeeSystem.com.website.accounting.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounting")
public class ASignController {

	/**
	 * @Format Web View
	 * @Description 取得簽呈設定畫面
	 */
	@RequestMapping("/sign.do")
	public String jumpAccountSignJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_sign";
	}

	/**
	 * @Format Web View
	 * @Description 取得簽呈查詢頁面
	 */
	@RequestMapping("/searchSign.do")
	public String jumpAccountsearchSignJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accouting_searchSign";
	}

	/**
	 * @Format Web View
	 * @Description 取得會計簽核頁面
	 */
	@RequestMapping("/aSign.do")
	public String jumpAccountASignJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_sign_check";
	}
}
