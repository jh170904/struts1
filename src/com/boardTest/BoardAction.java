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
	
	//boardDAO가 없으므로 CommonDAOImpl 객체 생성을 통해 CommonDAO 생성
	//전역변수로 dao 객체를 생성하게되면 오류가 발생한다.
	//CommonDAO dao = CommonDAOImpl.getInstance();
	//DB를 오픈하고 나서 메소드를 실행하면 실행은 되지만 dao를 초기화시킴. 두번째 실행하면 오류가 발생함
	//메소드안에 그때 그때 commonDAO를 생성해야 한다.
	
	//게시글 작성페이지
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
	
	//게시글 작성완료시
	public ActionForward created_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		BoardForm f = (BoardForm)form; //페이지에서 boardForm 데이터 넘어오는걸 downcast하여 f에 할당
		String mode = request.getParameter("mode");//save, updateok
		
		if(mode.equals("save")){
			//입력
			int maxNum=dao.getIntValue("boardTest.maxNum");	//boardTest_sqlMap.xml에 있는 것을 넣어준다
			
			f.setNum(maxNum+1);
			f.setIpAddr(request.getRemoteAddr());
			
			dao.insertData("boardTest.insertData", f);//f는 boardForm을 넘겨준다 parameterClass여기안에있는 경로
			dao=null;
			
		}else{
			//수정
			String pageNum = request.getParameter("pageNum");
			dao.updateData("boardTest.updateData", f);
			
			//pageNum을 넘기기위해 세션 사용
			HttpSession session = request.getSession();
			session.setAttribute("pageNum", pageNum);
		}
		return mapping.findForward("created_ok");
	}
	
	//게시판 리스트조회시
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
		//created가 아닌 update에서 진행시 getParameter로 pageNum이 안오고
		//세션으로 전달되므로 세션을 생성해서 받아야함
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
		//boardTest_sqlMap.xml 에 있는 select문 id="dataCount"에 hmap 매개변수와 함께 전달 
		
		if(totalDataCount!=0)
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start",start);
		hMap.put("end", end);
		//hMap에는 4개의 데이터 존재 (searchKey,searchValue,start,end)
		
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
		
		//조회수 증가시 숫자 1개만 변동되므로 매개변수가 updateData(string, object)인 메소드 사용
		dao.updateData("boardTest.hitCountUpdate", num);
		
		BoardForm dto= (BoardForm)dao.getReadData("boardTest.readData", num);
		if(dto==null)
			return mapping.findForward("list");
		
		int lineSu = dto.getContent().split("\n").length;
		
		dto.setContent(dto.getContent().replaceAll("\n", "<br/>"));
		
		//이전글 다음글
		String preUrl = "";
		String nextUrl = "";
		
		Map<String, Object> hMap = new HashMap<String, Object>();
		
		hMap.put("searchKey",searchKey);
		hMap.put("searchValue",searchValue);
		hMap.put("num", num);
		
		//이전글 제목
		String preSubject = "";
		//데이터 읽어오기
		BoardForm preDTO = (BoardForm)dao.getReadData("boardTest.preReadData",hMap);//downcast
		
		if(preDTO!=null){
			//주소
			preUrl = cp + "/boardTest.do?method=article&pageNum=" + pageNum;
			preUrl += "&num=" + preDTO.getNum();
			//제목
			preSubject = preDTO.getSubject();
		}
		
		//다음글 제목
		String nextSubject = "";
		//데이터 읽어오기
		BoardForm nextDTO = (BoardForm)dao.getReadData("boardTest.nextReadData",hMap);//downcast
		
		if(nextDTO!=null){
			//주소
			nextUrl = cp + "/boardTest.do?method=article&pageNum=" + pageNum;
			nextUrl += "&num=" + nextDTO.getNum();
			//제목
			nextSubject = nextDTO.getSubject();
		}
		
		//페이징할때 bbsArticle_footer에 있는 버튼 중
		//리스트로 다시 돌아오는 기본주소
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
		
		//수정과 삭제에서 사용할 인수
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
		
		//dao의 sql문 실행
		dao.deleteData("boardTest.deleteData", num);
		
		//원래 페이지로 이동을 하기 위해 pageNum 받아야함. 세션으로 받음
		HttpSession session = request.getSession();
		session.setAttribute("pageNum", pageNum);
		
		return mapping.findForward("delete_ok");
	}

}
