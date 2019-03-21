package com.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class TestAction extends Action{

	//execute�� ������ ������� ����
	//struts�� �� execute ��ɺ��� �� ���� ����� ����Ѵ�.
	//if���� �� �˻��ϰ� �Ѿ�� ���� �ӵ��� ������. 
	//�� if-else if���� �����۾��� �ؼ� �޼ҵ�� ������.  
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String uri = request.getRequestURI();
		
		if(uri.indexOf("/test_ok.do")!=-1){
		
			TestForm f = (TestForm)form;//�ڹٰ� �Ѱ��ִ� ���̹Ƿ� object�� ���޵ǹǷ� �ٿ�ĳ��Ʈ �ʿ�
			request.setAttribute("vo", f);
			
			return mapping.findForward("ok");//ok�� ���ڸ� ������ ��������
		}
		return mapping.findForward("error");
	}
	
}
