package com.boardTest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class BoardAction extends DispatchAction{
	
	//boardDAO�� �����Ƿ� CommonDAOImpl ��ü ������ ���� CommonDAO ����
	//���������� dao ��ü�� �����ϰԵǸ� ������ �߻��Ѵ�.
	//CommonDAO dao = CommonDAOImpl.getInstance();
	//DB�� �����ϰ� ���� �޼ҵ带 �����ϸ� ������ ������ dao�� �ʱ�ȭ��Ŵ. �ι�° �����ϸ� ������ �߻���
	//�޼ҵ�ȿ� �׶� �׶� commonDAO�� �����ؾ� �Ѵ�.
	
	//�Խñ� �ۼ�������
	public ActionForward created(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String mode = request.getParameter("mode");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		String paramArticle = "";
		
		if(searchValue==null){
			searchKey = "subject";
			searchValue = "";
		}
		if(request.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}
		
		if(mode==null){
			//insert
			CommonDAO dao = CommonDAOImpl.getInstance();
			request.setAttribute("mode", "save");
		}else{
			//update
			CommonDAO dao = CommonDAOImpl.getInstance();
			int num = Integer.parseInt(request.getParameter("num"));
			String pageNum = request.getParameter("pageNum");
			
			BoardForm dto = (BoardForm)dao.getReadData("boardTest.readData",num);
			if(dto==null){
				return mapping.findForward("list");
			}
			request.setAttribute("dto", dto);
			request.setAttribute("mode", "updateok");
			request.setAttribute("pageNum", pageNum);
		}
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			paramArticle += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
		}
		request.setAttribute("paramArticle", paramArticle);
		return mapping.findForward("created");
	}
	
	//�Խñ� �ۼ��Ϸ��
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		BoardForm f = (BoardForm)form; //���������� boardForm ������ �Ѿ���°� downcast�Ͽ� f�� �Ҵ�
		String mode = request.getParameter("mode");//save, updateok
		
		if(mode.equals("save")){
			//�Է�
			int maxNum=dao.getIntValue("boardTest.maxNum");	//boardTest_sqlMap.xml�� �ִ� ���� �־��ش�
			
			f.setNum(maxNum+1);
			f.setIpAddr(request.getRemoteAddr());
			
			dao.insertData("boardTest.insertData", f);//f�� boardForm�� �Ѱ��ش� parameterClass����ȿ��ִ� ���
			dao=null;
			
		}else{
			//����
			String pageNum = request.getParameter("pageNum");
			dao.updateData("boardTest.updateData", f);
			
			//pageNum�� �ѱ������ ���� ���
			HttpSession session = request.getSession();
			session.setAttribute("pageNum", pageNum);
		}
		return mapping.findForward("created_ok");
	}
	
	//�Խ��� ����Ʈ��ȸ��
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = request.getContextPath();
		MyUtil myUtil = new MyUtil();
		
		int numPerPage = 10;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		//created�� �ƴ� update���� ����� getParameter�� pageNum�� �ȿ���
		//�������� ���޵ǹǷ� ������ �����ؼ� �޾ƾ���
		HttpSession session = request.getSession();
		if(pageNum==null){
			pageNum = (String)session.getAttribute("pageNum");
		}
		
		if(pageNum!=null)
			currentPage = Integer.parseInt(pageNum);
		
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		hMap.put("searchKey",searchKey);
		hMap.put("searchValue",searchValue);
		
		totalDataCount = dao.getIntValue("boardTest.dataCount", hMap);
		//boardTest_sqlMap.xml �� �ִ� select�� id="dataCount"�� hmap �Ű������� �Բ� ���� 
		
		if(totalDataCount!=0)
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start",start);
		hMap.put("end", end);
		//hMap���� 4���� ������ ���� (searchKey,searchValue,start,end)
		
		List<Object> lists = dao.getListData("boardTest.listData", hMap);
		
		String param = "";
		String urlArticle = "";
		String urlList = "";
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			param = "&searchKey=" + searchKey;
			param += "&searchValue=" + searchValue; 
		}
		
		urlList = cp + "/boardTest.do?method=list" + param;
		urlArticle = cp + "/boardTest.do?method=article&pageNum="+ currentPage;
		urlArticle += param;
		
		String params = param + "&pageNum=" + currentPage;
		request.setAttribute("lists", lists);
		request.setAttribute("urlArticle", urlArticle);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		request.setAttribute("totalDataCount", totalDataCount);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("params", params);
		return mapping.findForward("list");
	}
	
	public ActionForward article(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		String cp = request.getContextPath();
		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		String searchKey = request.getParameter("searchKey");
		String searchValue = request.getParameter("searchValue");
		
		if(searchValue==null){
			searchKey = "subject";
			searchValue = "";
		}
		
		if(request.getMethod().equalsIgnoreCase("GET")){
			searchValue = URLDecoder.decode(searchValue,"UTF-8");
		}
		
		//��ȸ�� ������ ���� 1���� �����ǹǷ� �Ű������� updateData(string, object)�� �޼ҵ� ���
		dao.updateData("boardTest.hitCountUpdate", num);
		
		BoardForm dto= (BoardForm)dao.getReadData("boardTest.readData", num);
		if(dto==null)
			return mapping.findForward("list");
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\n", "<br/>"));
		
		//������ ������
		String preUrl = "";
		String nextUrl = "";
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		hMap.put("searchKey",searchKey);
		hMap.put("searchValue",searchValue);
		hMap.put("num", num);
		
		//������ ����
		String preSubject = "";
		//������ �о����
		BoardForm preDTO = (BoardForm)dao.getReadData("boardTest.preReadData",hMap);//downcast
		
		if(preDTO!=null){
			//�ּ�
			preUrl = cp + "/boardTest.do?method=article&pageNum=" + pageNum;
			preUrl += "&num=" + preDTO.getNum();
			//����
			preSubject = preDTO.getSubject();
		}
		
		//������ ����
		String nextSubject = "";
		//������ �о����
		BoardForm nextDTO = (BoardForm)dao.getReadData("boardTest.nextReadData",hMap);//downcast
		
		if(nextDTO!=null){
			//�ּ�
			nextUrl = cp + "/boardTest.do?method=article&pageNum=" + pageNum;
			nextUrl += "&num=" + nextDTO.getNum();
			//����
			nextSubject = nextDTO.getSubject();
		}
		
		//����¡�Ҷ� bbsArticle_footer�� �ִ� ��ư ��
		//����Ʈ�� �ٽ� ���ƿ��� �⺻�ּ�
		String urlList = cp + "/boardTest.do?method=list&pageNum=" + pageNum;
		
		if(!searchValue.equals("")){
			searchValue = URLEncoder.encode(searchValue,"UTF-8");
			urlList += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			
			if(!preUrl.equals("")){
				preUrl += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			}
			
			if(!nextUrl.equals("")){
				nextUrl += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
			}
		}
		
		//������ �������� ����� �μ�
		String paramArticle = "num=" + num + "&pageNum=" + pageNum;
		if(!searchValue.equals("")){
			paramArticle += "&searchKey=" + searchKey + "&searchValue=" + searchValue;
		}
		//Model
		request.setAttribute("dto", dto);
		request.setAttribute("preSubject", preSubject);
		request.setAttribute("preUrl", preUrl);
		request.setAttribute("nextSubject",nextSubject );
		request.setAttribute("nextUrl", nextUrl);
		request.setAttribute("lineSu", lineSu);
		request.setAttribute("paramArticle", paramArticle);
		request.setAttribute("urlList", urlList);
		
		return mapping.findForward("article");
	}
	
	public ActionForward deleted(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();		
		int num = Integer.parseInt(request.getParameter("num"));
		String pageNum = request.getParameter("pageNum");
		
		//dao�� sql�� ����
		dao.deleteData("boardTest.deleteData", num);
		
		//���� �������� �̵��� �ϱ� ���� pageNum �޾ƾ���. �������� ����
		HttpSession session = request.getSession();
		session.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("delete_ok");
	}

}
