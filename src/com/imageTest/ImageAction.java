package com.imageTest;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.util.FileManager;
import com.util.MyUtil;
import com.util.dao.CommonDAO;
import com.util.dao.CommonDAOImpl;

public class ImageAction extends DispatchAction{
	
	//이미지 등록 페이지
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return mapping.findForward("write");
		
	}
	
	//파일 업로드 등록
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		ImageForm f = (ImageForm)form;
		
		//파일 업로드
		String newFileName = FileManager.doFileUpload(f.getUpload(), savePath);
		
		//파일에 대한 정보 추출후 DB반영
		if(newFileName!=null){
			int maxNum = dao.getIntValue("img.maxNum");
			f.setNum(maxNum+1);
			f.setSaveFileName(newFileName);
			f.setOriginalFileName(f.getUpload().getFileName());
			dao.insertData("img.insertData", f);
		}
		return mapping.findForward("write_ok");
	}
	
	//파일 리스트
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		MyUtil myUtil = new MyUtil();
		String cp = request.getContextPath();
		
		int numPerPage = 9;
		int totalPage = 0;
		int totalDataCount = 0;
		
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		if(pageNum!=null&&!pageNum.equals("")){
			currentPage = Integer.parseInt(pageNum);
		}
		
		totalDataCount = dao.getIntValue("img.dataCount");
		
		if(totalDataCount!=0)
			totalPage = myUtil.getPageCount(numPerPage, totalDataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		Map<String, Object> hMap = new HashMap<String,Object>();
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		hMap.put("start", start);
		hMap.put("end", end);
		
		List<Object> lists = (List<Object>)dao.getListData("img.listData",hMap);
		
		//번호 재정렬 작업
		Iterator<Object> it = lists.iterator();
		
		int listNum,n=0;
		String str;
		
		while(it.hasNext()){
			
			//전체 데이터 갯수 6개일 때 
			//리스트번호가 10 8 6 (start=1)/ 5 4 3(start=4) 라 가정
			// 1 2 3 / 4 5 6
			ImageForm dto = (ImageForm)it.next();
			listNum = totalDataCount - (start + n - 1) ;
			dto.setListNum(listNum);
			n++;
			
			//파일 다운로드 경로
			str = cp + "/img.do?method=download&num="+dto.getNum();
			dto.setUrlFile(str);
		}
		
		String urlList = cp + "/img.do?method=list";
		
		// 이미지파일경로
		String imagePath = cp + "/pds/imageFile";
		request.setAttribute("imagePath", imagePath);
		
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("lists", lists);
		request.setAttribute("pageNum", pageNum);		
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("totalDataCount", totalDataCount);
		request.setAttribute("pageIndexList", myUtil.pageIndexList(currentPage, totalPage, urlList));
		
		return mapping.findForward("list");
	}
	
	//파일 삭제
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//DB연결 및 세션가져오기
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		//삭제해야하는 데이터 값 읽어오기
		ImageForm dto = (ImageForm)dao.getReadData("img.readData", num);
		String saveFileName = dto.getSaveFileName();
		
		//파일삭제
		FileManager.doFileDelete(saveFileName, savePath);
		//DB삭제
		dao.deleteData("img.deleteData",num);
		
		return mapping.findForward("delete");
	}
	
	//파일 다운로드
	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//DB연결 및 세션가져오기
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		//다운로드 받을 데이터값 읽어오기
		ImageForm dto = (ImageForm)dao.getReadData("img.readData", num);
		
		if(dto==null){
			return mapping.findForward("list");
		}
		
		//다운로드 진행
		boolean flag = FileManager.doFileDownload(dto.getSaveFileName(),
				dto.getOriginalFileName(), savePath, response);
		
		if(!flag){
			//다운로드 받지 못했을 시 실행
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script type='text/javascript'>");
			out.print("alert('다운로드 에러!!!');");
			out.print("history.back();");
			out.print("</script>");
		}
		//다운로드 후 페이지 변동이 없으므로 반환값을 null로 줌		
		return mapping.findForward(null);
	}

}
