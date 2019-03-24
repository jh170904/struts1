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
	
	//�̹��� ��� ������
	public ActionForward write(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return mapping.findForward("write");
		
	}
	
	//���� ���ε� ���
	public ActionForward write_ok(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		ImageForm f = (ImageForm)form;
		
		//���� ���ε�
		String newFileName = FileManager.doFileUpload(f.getUpload(), savePath);
		
		//���Ͽ� ���� ���� ������ DB�ݿ�
		if(newFileName!=null){
			int maxNum = dao.getIntValue("img.maxNum");
			f.setNum(maxNum+1);
			f.setSaveFileName(newFileName);
			f.setOriginalFileName(f.getUpload().getFileName());
			dao.insertData("img.insertData", f);
		}
		return mapping.findForward("write_ok");
	}
	
	//���� ����Ʈ
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
		
		//��ȣ ������ �۾�
		Iterator<Object> it = lists.iterator();
		
		int listNum,n=0;
		String str;
		
		while(it.hasNext()){
			
			//��ü ������ ���� 6���� �� 
			//����Ʈ��ȣ�� 10 8 6 (start=1)/ 5 4 3(start=4) �� ����
			// 1 2 3 / 4 5 6
			ImageForm dto = (ImageForm)it.next();
			listNum = totalDataCount - (start + n - 1) ;
			dto.setListNum(listNum);
			n++;
			
			//���� �ٿ�ε� ���
			str = cp + "/img.do?method=download&num="+dto.getNum();
			dto.setUrlFile(str);
		}
		
		String urlList = cp + "/img.do?method=list";
		
		// �̹������ϰ��
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
	
	//���� ����
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//DB���� �� ���ǰ�������
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		//�����ؾ��ϴ� ������ �� �о����
		ImageForm dto = (ImageForm)dao.getReadData("img.readData", num);
		String saveFileName = dto.getSaveFileName();
		
		//���ϻ���
		FileManager.doFileDelete(saveFileName, savePath);
		//DB����
		dao.deleteData("img.deleteData",num);
		
		return mapping.findForward("delete");
	}
	
	//���� �ٿ�ε�
	public ActionForward download(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//DB���� �� ���ǰ�������
		CommonDAO dao = CommonDAOImpl.getInstance();
		HttpSession session = request.getSession();
		
		String root = session.getServletContext().getRealPath("/");
		String savePath = root + File.separator + "pds" +  File.separator + "imageFile";
		
		int num = Integer.parseInt(request.getParameter("num"));
		
		//�ٿ�ε� ���� �����Ͱ� �о����
		ImageForm dto = (ImageForm)dao.getReadData("img.readData", num);
		
		if(dto==null){
			return mapping.findForward("list");
		}
		
		//�ٿ�ε� ����
		boolean flag = FileManager.doFileDownload(dto.getSaveFileName(),
				dto.getOriginalFileName(), savePath, response);
		
		if(!flag){
			//�ٿ�ε� ���� ������ �� ����
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.print("<script type='text/javascript'>");
			out.print("alert('�ٿ�ε� ����!!!');");
			out.print("history.back();");
			out.print("</script>");
		}
		//�ٿ�ε� �� ������ ������ �����Ƿ� ��ȯ���� null�� ��		
		return mapping.findForward(null);
	}

}
