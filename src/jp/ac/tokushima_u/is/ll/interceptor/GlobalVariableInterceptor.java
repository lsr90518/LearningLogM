package jp.ac.tokushima_u.is.ll.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.tokushima_u.is.ll.service.PropertyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class GlobalVariableInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private PropertyService propertyService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute("ctx", request.getContextPath());
		request.setAttribute("projectName", propertyService.getProjectName());
		return true;
	}
}
