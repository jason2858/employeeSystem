package employeeSystem.com.website.system.aop;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import employeeSystem.com.website.system.exception.SystemOutException;
import net.sf.json.JSONObject;

@Aspect
@Component
public class SystemInterceptor {
	@Autowired
	private HttpServletRequest request;

	@Around("execution(* com.yesee.gov.website.controller.rest.RestPunchNoticeController..*(..))")
	public void systemAop(ProceedingJoinPoint pjp) throws Exception, Throwable {
		JSONObject result = new JSONObject();
		HttpServletResponse rep = null;
		PrintWriter out = null;
		try {

			if (pjp.getArgs() != null) {
				for (Object object : pjp.getArgs()) {
					if (object instanceof HttpServletResponse) {
						rep = (HttpServletResponse) object;
					}
				}
			}

			if (rep != null) {
				rep.setContentType("text/html; charset=UTF-8");
				rep.setCharacterEncoding("UTF-8");
				rep.setHeader("Pragma", "No-Cache");
				rep.setHeader("Cache-Control", "No-Cache");

				out = rep.getWriter();
			}

			Response response = (Response) pjp.proceed();
			String data = "";
			if (response.getEntity() == null) {
			} else {
				data = response.getEntity().toString();
			}
			result.put("status", "200");
			result.put("message", "執行成功");
			result.put("data", data);
		} catch (SystemOutException sout) {
			result.put("status", "700");
			result.put("message", sout.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "500");
			result.put("message", "系統異常: " + e.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			result.put("status", "500");
			result.put("message", "系統異常: " + e.getMessage());
		} finally {
			if (out != null) {
				out.print(result.toString());
				out.flush();
				out.close();
			}
		}
	}
}