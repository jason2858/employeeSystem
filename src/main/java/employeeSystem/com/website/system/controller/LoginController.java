package employeeSystem.com.website.system.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping("/timeout.do")
	public String loginError(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		return "timeout";
	}
}