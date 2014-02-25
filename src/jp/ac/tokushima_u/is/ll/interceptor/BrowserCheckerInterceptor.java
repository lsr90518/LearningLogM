package jp.ac.tokushima_u.is.ll.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class BrowserCheckerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if (!userAgent.contains("webkit") || !userAgent.contains("chrome")) {
				response.sendRedirect(request.getContextPath() + "/errors/browser.jsp");
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

}
