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

	//회원가입,수정 페이지 포워드
	public ActionForward created(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//입력,수정
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
				request.setAttribute("message", "일치하는 회원정보가 없습니다!");
				return mapping.findForward("login");
			}
			request.setAttribute("dto", dto);
			request.setAttribute("mode", "updateok");
		}
		return mapping.findForward("created");
	
	}
	
	//회원데이터 입력완료시
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form; //페이지에서 MemberForm 데이터 넘어오는걸 downcast하여 f에 할당
		String mode = request.getParameter("mode");//save, updateok
		
		if(mode.equals("save")){
			//입력
			if(dao.getReadData("member.getId",f)==null){
				dao.insertData("member.insertData", f);
				dao=null;
			}else{
				request.setAttribute("message", "동일한 아이디가 존재합니다!!");
				request.setAttribute("mode", "save");
				return mapping.findForward("created");
			}
			
		}else{
			//수정
			dao.updateData("member.updateData", f);
		}
		return mapping.findForward("created_ok");
	}
	
	//로그인 페이지 포워드
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return mapping.findForward("login");
		
	}
	
	//로그인정보 입력시
	public ActionForward login_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form;
		f = (MemberForm)dao.getReadData("member.getReadData",f);
		//dto==null일 경우 아이디가 없음
		if(f==null){
			request.setAttribute("message", "아이디 또는 패스워드를 정확히 입력하세요!");
			return mapping.findForward("login");
		}
		
		//세션 올리기
		HttpSession session = request.getSession();
		session.setAttribute("MemberForm", f);
		
		return mapping.findForward("login_ok");
	}
	
	//비밀번호찾기 페이지
	public ActionForward searchpw(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
 		return mapping.findForward("searchpw");
	}
	
	//비밀번호 찾기
	public ActionForward searchpw_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		CommonDAO dao = CommonDAOImpl.getInstance();
		MemberForm f = (MemberForm)form;
		MemberForm dto = (MemberForm)dao.getReadData("member.getPwd",f);
		
		//dto==null일 경우 해당정보 없음
		if(dto==null){
			request.setAttribute("message", "해당하는 정보가 없어요!");
			return mapping.findForward("searchpw");
		}else{
			String str = "비밀번호는 ["+dto.getUserPwd()+"]입니다.";
			request.setAttribute("message", str);
			return mapping.findForward("searchpw");
		}
	}

}
