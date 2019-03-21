package com.util.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CharsetEncodingFilter implements Filter{
	
	private String charset;
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String uri;
		
		if(request instanceof HttpServletRequest){
			
			//downcast
			HttpServletRequest req = (HttpServletRequest)request;
			
			uri = req.getRequestURI();
			
			if(req.getMethod().equalsIgnoreCase("POST")){
				
				if(uri.indexOf("ajax.do")!=-1){
					//ajax.do 페이지를 요청할 경우 인코딩 방식을 euc-kr로 설정 
					req.setCharacterEncoding("euc-kr");
				}else{
					req.setCharacterEncoding("UTF-8");
				}
			}
			//다른필터가 있다면 그 필터를 찾아감. 현재(필터2개존재)는 (TimerFilter를 거친 후)서버 찾아감
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		charset = config.getInitParameter("charset");
	}

}
