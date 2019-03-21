package com.util;

public class MyUtil {
	
	//전체페이지수 구하기
	//numPerPage : 한화면에 표시할 데이터 갯수
	//dataCount : 전체 데이터 갯수
	public int getPageCount(int numPerPage, int dataCount){
		
		int pageCount=0;
		pageCount = dataCount/numPerPage;
		
		if(dataCount % numPerPage!=0){ //나머지가 0이 아니면 페이지 하나 더 만듦
			pageCount++;
		}
		return pageCount;
	}
	
	//페이징 처리 메소드
	//currentPage : 현재 표시할 페이지
	//totalPage : 전체 페이지 수
	//listUrl : 링크를 설정할 URL(list.jsp)
	public String pageIndexList(int currentPage, int totalPage, String listUrl){
		
		int numPerBlock = 5; 	//리스트밑에 나오는 페이지번호 출력 갯수
		int currentPageSetup;	//표시할 첫 페이지의 -1 해준 값
		int page; 				//하이퍼링크가 될 page index 숫자					
		
		StringBuffer sb = new StringBuffer();
		if(currentPage==0 || totalPage==0){ //데이터 없을 경우
			return "";
		}
		
		//list.jsp
		//list.jsp?searchKey=name&searchValue=suzi 검색한내용의 인수와 변수값이 붙음

		if(listUrl.indexOf("?")!=-1){//물음표가 있으면
			listUrl = listUrl + "&";
			//list.jsp?searchKey=name&searchValue=suzi&
		}else{
			listUrl = listUrl + "?";
			//list.jsp?
		}
		
		//표시할 첫페이지에서 -1 한 값 
		currentPageSetup = (currentPage/numPerBlock)*numPerBlock;
		
		//currentPage가 numPerBlock으로 나누어 떨어질 경우
		if(currentPage % numPerBlock == 0)
			currentPageSetup = currentPageSetup - numPerBlock ;
		
		//◀이전 버튼
		if(totalPage>numPerBlock && currentPageSetup>0){
			sb.append("<a href=\""+listUrl + "pageNum=" + currentPageSetup + "\">◀이전</a>&nbsp;" );
			//<a href="list.jsp?pageNum=5">◀이전</a>&nbsp; 역슬래시로 쌍따옴표가 문자로 인식되게함
		}
		
		//바로가기 페이지
		page = currentPageSetup+1;//currentPageSetup에 +1한 값 부터 페이지번호가 시작됨
		
		//바로가기 페이지는 전체페이지수보다 커질수없음 
		while(page<=totalPage && page<=(currentPageSetup+numPerBlock)){
			if(page == currentPage){
				sb.append("<font color=\"Fuchsia\">"+page+"</font>&nbsp;");
				//<font color="Fuchsia">9</font>&nbsp;
			}else{
				sb.append("<a href=\""+ listUrl + "pageNum=" + page+ "\">"+page+"</a>&nbsp;");
				//<a href="list.jsp?pageNum=10">10</a>&nbsp;
			}
			page++;
		}
		
		//다음▶,마지막페이지
		if(totalPage - currentPageSetup > numPerBlock){
			//총 페이지 12일때, 현재 페이지가 10페이지라면  
			//numPerBlock 5 보다 작으므로 다음버튼 생성하지않음
			sb.append("<a href=\"" + listUrl +"pageNum=" +page + "\">다음▶</a>&nbsp;" );
			//<a href="list.jsp?pageNum=11">다음▶</a>&nbsp;
		}
		return sb.toString();
	}
}
