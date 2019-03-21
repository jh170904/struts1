package com.board;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.DBCPConn;
import com.util.MyUtil;

public class BoardAction extends DispatchAction{//서블릿 역할(Controller)
	
	Connection conn = DBCPConn.getConnection();
	BoardDAO dao = new BoardDAO(conn);
	
	//메소드명 변경 가능
	//게시판 작성시
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//사용자가 오게되면 create페이지를 띄우고자 함.
		//입력폼
		return mapping.findForward("created");
		//created라는 문자열을 가지고 돌아가라
	}
	
	//게시판 작성완료시
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		BoardForm f = (BoardForm)form; //downcast
		f.setNum(dao.getMaxNum()+1);
		f.setIpAddr(request.getRemoteAddr());
		dao.insertData(f);
		
		return mapping.findForward("save");
	}
	
	//게시판 리스트 조회시
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cp = request.getContextPath();
		MyUtil myUtil = new MyUtil();
		int numPerPage = 10;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = request.getParameter("pageNum");
		int currentPage = 1;
		if(pageNum!=null)
			currentPage = Integer.parseInt(pageNum);
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET"))
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		
		totalDataCount = dao.getDataCount(searchKey, searchValue);
		
		if(totalDataCount!=0)
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		List<BoardForm> lists = dao.getLists(start, end, searchKey, searchValue);
		
		String param = "";
		String urlArticle = "";
		String urlList = "";
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			param = "&searchKey="+searchKey;
			param += "&searchValue=" + searchValue;
		}
		
		urlList = cp + "/board.do?method=list" +param;
		urlArticle = cp +"/board.do?method=article&pageNum="+currentPage; 
		urlArticle += param;
		
		request.setAttribute("lists", lists);
		request.setAttribute("urlArticle", urlArticle);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("totalDataCount", totalDataCount);
		//이 데이터를 가지고 + list라는 단어와 함께 포워딩됨
		return mapping.findForward("list");
	}
	
	//하나의 게시글 조회시
	public ActionForward article(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//리스트페이지에서 
		//localhost:8080/struts1/board.do?method=article&pageNum=1&searchKey=subject&searchValue=1&num=19
		//넘겨오는 주소에서의 파라미터값을 받아주는 작업 필요
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null)
			searchValue = URLDecoder.decode(searchValue,"UTF-8");//파라미터값 받을때 디코딩
		
		dao.updateHitCount(num);
		BoardForm dto = dao.getReadData(num);
		
		if(dto==null)
			return mapping.findForward("list");
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\n","<br/>"));
		String param = "pageNum="+pageNum;
		if(searchValue!=null){
			param += "&searchKey="+searchKey;
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//보낼때 다시 인코딩
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("params", param);
		request.setAttribute("lineSu", lineSu);
		request.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("article");
	}
	
	//하나의 게시글을 수정하기 위한 입력페이지
	public ActionForward updated(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		BoardForm dto = dao.getReadData(num);
		
		if(dto==null)
			return mapping.findForward("list");
		
		dto.setContent(dto.getContent().replaceAll("\n","<br/>"));
		String param = "";
		if(searchValue!=null){
			param += "&searchKey="+searchKey;
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//보낼때 다시 인코딩
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("params", param);
		request.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("updated");
	}
	
	//하나의 게시글 수정시
	public ActionForward updated_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		BoardForm f = (BoardForm)form;
		f.setNum(num);
		dao.updateData(f);
		
		String param = "";
		if(searchValue!=null){
			param += "&searchKey="+searchKey;
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//보낼때 다시 인코딩
		}
		
		//받는 페이지가 없으므로 setAttribute를 작성할 필요없음
		//request.setAttribute("params", param);
		//request.setAttribute("pageNum", pageNum);
		
		//return mapping.findForward("updated_ok");
		//<forward name="updated_ok" redirect="true" path="/board.do?method=list"/>
		//config에 작성하는 forward 경로와 ActionForward 객체는 동일한 개념
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+pageNum+param);
		return af;
	}
	
	//하나의 게시글 삭제시
	public ActionForward deleted(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");

		dao.deleteData(num);
		
		String param = "";
		if(searchValue!=null){
			param += "&searchKey="+searchKey;
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//보낼때 다시 인코딩
		}
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+pageNum+param);
		return af;
	}
}
