package employeeSystem.com.website.accounting.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VoucherController {

	@RequestMapping("/voucher/addVoucherM.do")
	public String jumpAddVoucherMJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_addVoucher_m";
	}

	@RequestMapping("/voucher/addVoucher.do")
	public String jumpAddVoucherJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_addVoucher";
	}

	@RequestMapping("/voucher/voucherSearch.do")
	public String jumpVoucherSearchJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_searchVoucher";
	}

	@RequestMapping("/voucher/voucherDSearch.do")
	public String jumpVvoucherModifyJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_searchVoucherD";
	}
}
