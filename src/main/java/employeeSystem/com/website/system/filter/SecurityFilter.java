package com.yesee.gov.website.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

public class SecurityFilter implements Filter {
	private static final Logger logger = LogManager.getLogger(SecurityFilter.class);

	private static final String[] RESOURCE_URL = { ".png", ".js", ".css", "ico", ".gif", ".woff2", ".jpg" };
	private static final String[] HOME_URL = { "/", "/YeseeGov/" };
	private static final String[] METHOD_URL = { "timeoutCheck" // 連線逾時
			, "getMakeUpCount" // 取得補打卡次數
			, "directSign" // mail 簽合
			, "directReject" // mail 駁回
			, "directDelete" // mail 刪除
			, "getAuthorise" // 取得權限
			, "accountCheck" // 確認登入資料
			, "logout.do" };

	@Override
	public void destroy() {
	}

	@RequestMapping("/*")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String requestURI = request.getRequestURI();
		HttpSession session = request.getSession();
//		if (!Arrays.asList(RESOURCE_URL).stream().anyMatch(str -> requestURI.contains(str))) {
//			logger.info("====>SecurityFilter.doFilter:" + requestURI);
//		}
		if (Arrays.asList(HOME_URL).stream().anyMatch(str -> requestURI.equals(str))) {
			chain.doFilter(req, res);
		} else if (Arrays.asList(RESOURCE_URL).stream().anyMatch(str -> requestURI.contains(str))) {
			chain.doFilter(req, res);
		} else if (Arrays.asList(METHOD_URL).stream().anyMatch(str -> requestURI.contains(str))) {
			chain.doFilter(req, res);
		}
//		else if (session.getAttribute("Authorise") == null) {
//			req.getRequestDispatcher("/timeout.do").forward(req, res);
//		} 
		else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}