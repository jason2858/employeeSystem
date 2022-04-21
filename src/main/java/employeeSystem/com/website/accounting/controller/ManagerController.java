package employeeSystem.com.website.accounting.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
//public class ManagerController {
//	@RequestMapping("/accounting_manager.do")
//	public String jumpJSP(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		return "accounting_manager";
//	}
//}

@Controller
@RequestMapping("/accounting")
public class ManagerController {

	/**
	 * @Format Web View
	 * @Description 取得項目管理頁面
	 */
	@GetMapping("/manager.do")
	public String jumpManagerJSP(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return "/accounting_manager";
	}

}
