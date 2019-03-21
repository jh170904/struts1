package com.util;

public class MyUtil {
	
	//��ü�������� ���ϱ�
	//numPerPage : ��ȭ�鿡 ǥ���� ������ ����
	//dataCount : ��ü ������ ����
	public int getPageCount(int numPerPage, int dataCount){
		
		int pageCount=0;
		pageCount = dataCount/numPerPage;
		
		if(dataCount % numPerPage!=0){ //�������� 0�� �ƴϸ� ������ �ϳ� �� ����
			pageCount++;
		}
		return pageCount;
	}
	
	//����¡ ó�� �޼ҵ�
	//currentPage : ���� ǥ���� ������
	//totalPage : ��ü ������ ��
	//listUrl : ��ũ�� ������ URL(list.jsp)
	public String pageIndexList(int currentPage, int totalPage, String listUrl){
		
		int numPerBlock = 5; 	//����Ʈ�ؿ� ������ ��������ȣ ��� ����
		int currentPageSetup;	//ǥ���� ù �������� -1 ���� ��
		int page; 				//�����۸�ũ�� �� page index ����					
		
		StringBuffer sb = new StringBuffer();
		if(currentPage==0 || totalPage==0){ //������ ���� ���
			return "";
		}
		
		//list.jsp
		//list.jsp?searchKey=name&searchValue=suzi �˻��ѳ����� �μ��� �������� ����

		if(listUrl.indexOf("?")!=-1){//����ǥ�� ������
			listUrl = listUrl + "&";
			//list.jsp?searchKey=name&searchValue=suzi&
		}else{
			listUrl = listUrl + "?";
			//list.jsp?
		}
		
		//ǥ���� ù���������� -1 �� �� 
		currentPageSetup = (currentPage/numPerBlock)*numPerBlock;
		
		//currentPage�� numPerBlock���� ������ ������ ���
		if(currentPage % numPerBlock == 0)
			currentPageSetup = currentPageSetup - numPerBlock ;
		
		//������ ��ư
		if(totalPage>numPerBlock && currentPageSetup>0){
			sb.append("<a href=\""+listUrl + "pageNum=" + currentPageSetup + "\">������</a>&nbsp;" );
			//<a href="list.jsp?pageNum=5">������</a>&nbsp; �������÷� �ֵ���ǥ�� ���ڷ� �νĵǰ���
		}
		
		//�ٷΰ��� ������
		page = currentPageSetup+1;//currentPageSetup�� +1�� �� ���� ��������ȣ�� ���۵�
		
		//�ٷΰ��� �������� ��ü������������ Ŀ�������� 
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
		
		//������,������������
		if(totalPage - currentPageSetup > numPerBlock){
			//�� ������ 12�϶�, ���� �������� 10���������  
			//numPerBlock 5 ���� �����Ƿ� ������ư ������������
			sb.append("<a href=\"" + listUrl +"pageNum=" +page + "\">������</a>&nbsp;" );
			//<a href="list.jsp?pageNum=11">������</a>&nbsp;
		}
		return sb.toString();
	}
}
