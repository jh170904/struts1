package com.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class TestAction extends Action{

	//execute는 실제로 사용하지 않음
	//struts는 이 execute 기능보다 더 좋은 기능을 사용한다.
	//if문을 다 검색하고 넘어가는 것은 속도가 느리다. 
	//이 if-else if문을 분해작업을 해서 메소드로 나눈다.  
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String uri = request.getRequestURI();
		
		if(uri.indexOf("/test_ok.do")!=-1){
		
			TestForm f = (TestForm)form;//자바가 넘겨주는 것이므로 object로 전달되므로 다운캐스트 필요
			request.setAttribute("vo", f);
			
			return mapping.findForward("ok");//ok란 문자를 가지고 포워딩됨
		}
		return mapping.findForward("error");
	}
	
}
