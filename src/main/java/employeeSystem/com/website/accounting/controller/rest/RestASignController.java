package employeeSystem.com.website.accounting.controller.rest;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.accounting.service.ASignService;
import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "/rest/accounting", produces = "application/json;charset=UTF-8")
public class RestASignController {

	private static final Logger logger = LogManager.getLogger(RestASignController.class);

	@Autowired
	public ASignService aSignService;

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得傳單簽程。 透過aSignService取得資料並回傳至前端。
	 */
	@GetMapping(value = "/sign")
	public Response getVoucherSign(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(aSignService.getSignList(req, resp), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PostMapping(value = "/sign")
	public Response saveVoucherSign(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		aSignService.saveVoucherSign(req, body);
		return Response.ok().build();
	}

	@PutMapping(value = "/sign")
	public Response updateVoucherSign(HttpServletRequest req, HttpServletResponse resp, @RequestBody JSONObject body)
			throws Exception {
		return Response.ok(aSignService.updateVoucherSign(req, body), MediaType.APPLICATION_JSON_TYPE).build();
	}

	@RequestMapping("/sign/*")
	public Response sign(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String voucherNo = req.getRequestURI();
		String URI = "/accounting/aSign.do";
		voucherNo = voucherNo.replace("/rest/accounting/sign/", "");
		logger.info("Email sign accounting :");
		logger.info("voucherNo : " + voucherNo);
		req.getSession().setAttribute("voucher_no", voucherNo);
		resp.sendRedirect(URI);
		return Response.ok().build();
	}

	@GetMapping(value = "/sign/role")
	public Response getSignRole(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return Response.ok(aSignService.getSignRoleList(), MediaType.APPLICATION_JSON_TYPE).build();
	}
}
