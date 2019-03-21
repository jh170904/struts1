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

public class BoardAction extends DispatchAction{//���� ����(Controller)
	
	Connection conn = DBCPConn.getConnection();
	BoardDAO dao = new BoardDAO(conn);
	
	//�޼ҵ�� ���� ����
	//�Խ��� �ۼ���
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//����ڰ� ���ԵǸ� create�������� ������ ��.
		//�Է���
		return mapping.findForward("created");
		//created��� ���ڿ��� ������ ���ư���
	}
	
	//�Խ��� �ۼ��Ϸ��
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		BoardForm f = (BoardForm)form; //downcast
		f.setNum(dao.getMaxNum()+1);
		f.setIpAddr(request.getRemoteAddr());
		dao.insertData(f);
		
		return mapping.findForward("save");
	}
	
	//�Խ��� ����Ʈ ��ȸ��
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
		//�� �����͸� ������ + list��� �ܾ�� �Բ� ��������
		return mapping.findForward("list");
	}
	
	//�ϳ��� �Խñ� ��ȸ��
	public ActionForward article(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//����Ʈ���������� 
		//localhost:8080/struts1/board.do?method=article&pageNum=1&searchKey=subject&searchValue=1&num=19
		//�Ѱܿ��� �ּҿ����� �Ķ���Ͱ��� �޾��ִ� �۾� �ʿ�
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue!=null)
			searchValue = URLDecoder.decode(searchValue,"UTF-8");//�Ķ���Ͱ� ������ ���ڵ�
		
		dao.updateHitCount(num);
		BoardForm dto = dao.getReadData(num);
		
		if(dto==null)
			return mapping.findForward("list");
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\n","<br/>"));
		String param = "pageNum="+pageNum;
		if(searchValue!=null){
			param += "&searchKey="+searchKey;
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//������ �ٽ� ���ڵ�
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("params", param);
		request.setAttribute("lineSu", lineSu);
		request.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("article");
	}
	
	//�ϳ��� �Խñ��� �����ϱ� ���� �Է�������
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
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//������ �ٽ� ���ڵ�
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("params", param);
		request.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("updated");
	}
	
	//�ϳ��� �Խñ� ������
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
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//������ �ٽ� ���ڵ�
		}
		
		//�޴� �������� �����Ƿ� setAttribute�� �ۼ��� �ʿ����
		//request.setAttribute("params", param);
		//request.setAttribute("pageNum", pageNum);
		
		//return mapping.findForward("updated_ok");
		//<forward name="updated_ok" redirect="true" path="/board.do?method=list"/>
		//config�� �ۼ��ϴ� forward ��ο� ActionForward ��ü�� ������ ����
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+pageNum+param);
		return af;
	}
	
	//�ϳ��� �Խñ� ������
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
			param += "&searchValue="+URLEncoder.encode(searchValue,"UTF-8");//������ �ٽ� ���ڵ�
		}
		
		ActionForward af = new ActionForward();
		af.setRedirect(true);
		af.setPath("/board.do?method=list&pageNum="+pageNum+param);
		return af;
	}
}
