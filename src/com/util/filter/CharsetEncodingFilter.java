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
					//ajax.do �������� ��û�� ��� ���ڵ� ����� euc-kr�� ���� 
					req.setCharacterEncoding("euc-kr");
				}else{
					req.setCharacterEncoding("UTF-8");
				}
			}
			//�ٸ����Ͱ� �ִٸ� �� ���͸� ã�ư�. ����(����2������)�� (TimerFilter�� ��ģ ��)���� ã�ư�
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		charset = config.getInitParameter("charset");
	}

}
