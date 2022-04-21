package employeeSystem.com.website.accounting.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounting")
public class ClosedController {

	/**
	 * @Format Web View
	 * @Description 取得關帳頁面
	 */
	@GetMapping("/closed.do")
	public String jumpClosedJSP(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		return "/accounting_closed";
	}

}
