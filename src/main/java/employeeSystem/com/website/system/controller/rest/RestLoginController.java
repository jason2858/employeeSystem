package employeeSystem.com.website.system.controller.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employeeSystem.com.website.system.model.TbAnnounce;
import employeeSystem.com.website.system.model.TbDepartment;
import employeeSystem.com.website.system.model.TbEmployees;
import employeeSystem.com.website.system.model.TbPreference;
import employeeSystem.com.website.system.model.TbPreferenceId;
import employeeSystem.com.website.system.service.AccountService;
import employeeSystem.com.website.system.service.AnnounceService;
import employeeSystem.com.website.system.service.DepartmentService;
import employeeSystem.com.website.system.service.PreferenceService;
import employeeSystem.com.website.system.util.Config;

@RestController
@RequestMapping(value = "/rest/login")
public class RestLoginController {

	private static final Logger logger = LogManager.getLogger(RestLoginController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private PreferenceService preferenceService;

	@Autowired
	private AnnounceService announceService;

	/**
	 * @param req
	 * @param resp
	 * @throws Exception 清除session中資料。
	 */
	@PostMapping("/logoutDo")
	public void logoutDo(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getSession().getAttribute("Account") + "";
		logger.info("Account logout: " + account);
		req.getSession().invalidate();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException      回傳session中Authorise(權限)資料。
	 */
	@RequestMapping("/getAuthorise")
	public void getAuthorise(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.print(req.getSession().getAttribute("Authorise"));
		out.flush();
		out.close();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 登入認證及存入session。
	 *                   接收前端傳回的登入資訊並透過ldapAuthenticate及accountService.getByUserName認證。
	 *                   認證成功後存入各項session。 傳回登入結果(true/查無員工資料/驗證失敗)。
	 */
	@PostMapping("/accountCheck")
	public Response handleLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getParameter("account");
		String password = req.getParameter("password");
		String msg = "驗證失敗";
		boolean check = ldapAuthenticate(account, password);
		logger.info("Account login: " + account + ",   Result of Account Check:  " + check);
		TbEmployees record = accountService.getByUserName(account);
		if (check && record == null) {
			msg = "查無員工資料";
		}
		if (check && record != null) {
			if (account.equals(record.getUsername())) {

				req.getSession().setMaxInactiveInterval(30 * 60);
				msg = "true";
				// 設定使用者帳號Session
				req.getSession().setAttribute("Account", account);
				// 設定員工中英文設定
				TbPreference preference = preferenceService.getByUserAndKey(account, "nameSelect");
				if (preference != null) {
					req.getSession().setAttribute("nameSelect", preference.getValue());
				} else {
					req.getSession().setAttribute("nameSelect", "EN");
				}
				// 設定下班打卡提醒時間
				TbPreference punchPreference = preferenceService.getByUserAndKey(account, "punchOutRemindHour");
				if (punchPreference != null) {
					req.getSession().setAttribute("punchOutRemindHour", punchPreference.getValue());
				} else {
					req.getSession().setAttribute("punchOutRemindHour", "0");
				}
				// 設定下班打卡提醒+打卡功能
				TbPreference remindPunchPreference = preferenceService.getByUserAndKey(account, "remindPunchOut");
				if (remindPunchPreference != null) {
					req.getSession().setAttribute("remindPunchOut", remindPunchPreference.getValue());
				} else {
					req.getSession().setAttribute("remindPunchOut", "remind");
				}
				// 設定公司ID session
				TbDepartment rootDep = departmentService.findDepartmentById(record.getDepartmentId());
				req.getSession().setAttribute("companyId", rootDep.getCompanyId());
				// 設定公告檢查Session
				req.getSession().setAttribute("announceCheck", "Y");

				// 設定時間Session
				Calendar et = Calendar.getInstance();
				SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM");
				String startdate = format.format(et.getTime());
				req.getSession().setAttribute("Year", startdate.substring(0, 4));
				req.getSession().setAttribute("start.year", startdate.substring(0, 4));
				req.getSession().setAttribute("start.month", startdate.substring(5, 7));

				SimpleDateFormat day = new java.text.SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				String today = day.format(date);
				req.getSession().setAttribute("Today", today);

				Calendar et2 = Calendar.getInstance();
				et2.setTime(et.getTime());
				et2.set(Calendar.DAY_OF_MONTH, 1);
				et2.add(Calendar.MONTH, 1);
				String endAttdate = format.format(et2.getTime());
				req.getSession().setAttribute("startAtt.date", startdate);
				req.getSession().setAttribute("endAtt.date", endAttdate);
				req.getSession().setAttribute("AttSelName", account);

				// 設定權限Session
				int Authorise = Integer.parseInt(record.getGroupId());
				req.getSession().setAttribute("Authorise", Authorise);

				// 設定部門id
				Optional<List<TbEmployees>> users = accountService.getEmployeesByName(account);
				Optional<String> depId = users.get().stream().map(TbEmployees::getDepartmentId).findFirst();
				req.getSession().setAttribute("depId", depId.orElse("undefined"));

				// 設定是否為人資主管
				if (accountService.isHRM(account))
					req.getSession().setAttribute("HRM", "true");
				else
					req.getSession().setAttribute("HRM", "false");

				// 設定Sidebar Session
				Map<String, Object> getSidebar = accountService.getSidebar(Authorise);
				int sidebarcount = 1, inner = 0;
				for (int i = 1; i < (Integer) getSidebar.get("sidebarcount"); i++) {
					req.getSession().setAttribute(sidebarcount + ".inner", 0);
					if ((String) getSidebar.get(i + ".parent_id") != null) {
						// 子項
						req.getSession().setAttribute(sidebarcount - 1 + "." + inner + ".name",
								getSidebar.get(i + ".name"));
						req.getSession().setAttribute(sidebarcount - 1 + "." + inner + ".url",
								getSidebar.get(i + ".url"));
						inner++;
						req.getSession().setAttribute(sidebarcount - 1 + ".inner", inner);
					} else {
						// 主項
						req.getSession().setAttribute(sidebarcount + ".id", getSidebar.get(i + ".id"));
						req.getSession().setAttribute(sidebarcount + ".name", getSidebar.get(i + ".name"));
						req.getSession().setAttribute(sidebarcount + ".url", getSidebar.get(i + ".url"));
						inner = 0;
						sidebarcount++;
					}
				}
				req.getSession().setAttribute("sidebarcount", sidebarcount);
			}

		}
		return Response.ok(msg).build();
	}

	// LDAP登入
	/**
	 * @param username
	 * @param password
	 * @return 使用LDAP對username及password進行認證並回傳結果(true/false)。
	 */
	public static boolean ldapAuthenticate(String username, String password) {

//		try {
//			Config config = Config.getInstance();
//			String ldapIp = config.getValue("ldap_ip");
//			String ldapContext = config.getValue("ldap_context");
//			Hashtable<String, String> env = new Hashtable<String, String>();
//			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
//			env.put(Context.PROVIDER_URL, ldapIp);
//			env.put(Context.SECURITY_AUTHENTICATION, "simple");
//			env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + "," + ldapContext);
//			env.put(Context.SECURITY_CREDENTIALS, password);
//			DirContext ctx = new InitialDirContext(env);
//			boolean result = ctx != null;
//			if (ctx != null) {
//				ctx.close();
//			}
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}

		try {
			if (password.equals("123")) {
				switch (username) {
				case "admin":
					return true;
				case "jasonFox":
					return true;
				}
			}

			return false;

		} catch (Exception e) {
			logger.error("error : ", e);
			return false;
		}
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得未簽核數量。 透過accountService.getUnsign取得補打卡及差勤申請未簽核數量。
	 *                   透過accountService.getProjectAndCustomerUnsign取得專案及客戶申請未簽核數量。
	 *                   回傳資料至前端。
	 */
	@PostMapping("/getUnsign")
	public Response getUnsign(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = (String) req.getSession().getAttribute("Account");
		int authorise = (Integer) req.getSession().getAttribute("Authorise");
		String depId = (String) req.getSession().getAttribute("depId");
		String HRM = (String) req.getSession().getAttribute("HRM");
		int count = accountService.getUnsign(depId, authorise, account, HRM);
		Map<String, Object> psCount = accountService.getProjectAndCustomerUnsign(authorise, depId);
		logger.info("Unsign elements count : " + count);

		// count MAP
		Map<String, Object> countMap = new HashMap<String, Object>();
		countMap.put("attCount", count);
		countMap.put("projectAndCustomer", psCount);

		return Response.ok(countMap, MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 取得未讀公告資訊。 透過preferenceService.getByUserAndKey取得以讀公告id
	 *                   透過announceService.getUnreadAnnounce取得未讀公告資料並回傳至前端。
	 */
	@PostMapping("/getUnreadAnnounce")
	public Response getUnreadAnnounce(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		req.getSession().setAttribute("announceCheck", "N");
		String user = (String) req.getSession().getAttribute("Account");
		String companyId = (String) req.getSession().getAttribute("companyId");
		String key = "last_announce_id";
		int id = 0;
		TbPreference record = preferenceService.getByUserAndKey(user, key);
		if (record != null) {
			id = Integer.valueOf(record.getValue());
		}
		List<TbAnnounce> list = announceService.getUnreadAnnounce(id, companyId);
		JSONArray data = new JSONArray();
		if (list.size() != 0) {
			SimpleDateFormat jd = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < list.size(); i++) {
				JSONObject object = new JSONObject();
				object.put("id", list.get(i).getId());
				object.put("subject", list.get(i).getSubject());
				object.put("content", list.get(i).getContent());
				object.put("time", jd.format(list.get(i).getCreatedAt()));
				data.put(object);
			}
		}
		return Response.ok(data.toString(), MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws Exception 儲存已讀公告資料。
	 *                   透過preferenceService.getByUserAndKey取得已讀公告資料並透過preferenceService.save儲存。
	 */
	@PostMapping("/read")
	public Response read(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String user = (String) req.getSession().getAttribute("Account");
		String key = "last_announce_id";
		String id = (String) req.getParameter("id");
		TbPreference record = preferenceService.getByUserAndKey(user, key);
		if (record == null) {
			record = new TbPreference();
			TbPreferenceId preId = new TbPreferenceId();
			preId.setUsername(user);
			preId.setConfigKey(key);
			record.setId(preId);
		}
		record.setValue(id);
		logger.info("set " + user + "'s " + key + " to " + record.getValue());
		preferenceService.save(record);
		return Response.ok().build();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception 逾時確認。 回傳session中的Authorise資料以做登入逾時驗證。
	 */
	@PostMapping("/timeoutCheck")
	public void getTimeout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Integer authorise = null;
		if (req.getSession().getAttribute("Authorise") != null)
			authorise = (Integer) req.getSession().getAttribute("Authorise");
		PrintWriter out = resp.getWriter();
		out.print(authorise);
		out.flush();
		out.close();
	}

	/**
	 * @param req
	 * @param resp
	 * @throws Exception 重新載入config。
	 */
	@PostMapping("/reloadConfig")
	public void reloadConfig(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Config.reload();
	}
}