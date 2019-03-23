package com.join;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class MemberAction extends DispatchAction{

	//ȸ������,���� ������ ������
	public ActionForward created(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//�Է�,����
		String mode = request.getParameter("mode");
		
		if(mode==null){
			//insert
			request.setAttribute("mode", "save");
		}else{
			//update
			CommonDAO dao = CommonDAOImpl.getInstance();
			MemberForm f = (MemberForm)form;
			MemberForm dto = (MemberForm)dao.getReadData("member.getReadData",f);
			if(dto==null){
				request.setAttribute("message", "��ġ�ϴ� ȸ�������� �����ϴ�!");
				return mapping.findForward("login");
			}
			request.setAttribute("dto", dto);
			request.setAttribute("mode", "updateok");
		}
		return mapping.findForward("created");
	
	}
	
	//ȸ�������� �Է¿Ϸ��
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form; //���������� MemberForm ������ �Ѿ���°� downcast�Ͽ� f�� �Ҵ�
		String mode = request.getParameter("mode");//save, updateok
		
		if(mode.equals("save")){
			//�Է�
			if(dao.getReadData("member.getId",f)==null){
				dao.insertData("member.insertData", f);
				dao=null;
			}else{
				request.setAttribute("message", "������ ���̵� �����մϴ�!!");
				request.setAttribute("mode", "save");
				return mapping.findForward("created");
			}
			
		}else{
			//����
			dao.updateData("member.updateData", f);
		}
		return mapping.findForward("created_ok");
	}
	
	//�α��� ������ ������
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return mapping.findForward("login");
		
	}
	
	//�α������� �Է½�
	public ActionForward login_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form;
		f = (MemberForm)dao.getReadData("member.getReadData",f);
		//dto==null�� ��� ���̵� ����
		if(f==null){
			request.setAttribute("message", "���̵� �Ǵ� �н����带 ��Ȯ�� �Է��ϼ���!");
			return mapping.findForward("login");
		}
		
		//���� �ø���
		HttpSession session = request.getSession();
		session.setAttribute("MemberForm", f);
		
		return mapping.findForward("login_ok");
	}
	
	//��й�ȣã�� ������
	public ActionForward searchpw(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
 		return mapping.findForward("searchpw");
	}
	
	//��й�ȣ ã��
	public ActionForward searchpw_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form;
		MemberForm dto = (MemberForm)dao.getReadData("member.getPwd",f);
		
		//dto==null�� ��� �ش����� ����
		if(dto==null){
			request.setAttribute("message", "�ش��ϴ� ������ �����!");
			return mapping.findForward("searchpw");
		}else{
			String str = "��й�ȣ�� ["+dto.getUserPwd()+"]�Դϴ�.";
			request.setAttribute("message", str);
			return mapping.findForward("searchpw");
		}
	}

}
