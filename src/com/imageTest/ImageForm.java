package com.imageTest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class ImageForm extends ActionForm{

	private static final long serialVersionUID = 1L;
	
	private int num;
	private String subject;
	private String saveFileName;
	private String originalFileName;
	
	//아파치에서 제공하는 파일 업로드시 사용하는 클래스
	//jsp페이지에서의 input 데이터의 name과 반드시 일치해야 한다.
	//파일을 2개 이상 올릴 경우 배열로 처리해서 진행하면 된다.
	private FormFile upload;
	
	//일렬번호 재정렬. 중간에 있는 파일이 삭제되더라도 일련번호가 정렬되도록
	private int listNum;
	//파일의 다운로드 경로
	private String urlFile;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSaveFileName() {
		return saveFileName;
	}
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public FormFile getUpload() {
		return upload;
	}
	public void setUpload(FormFile upload) {
		this.upload = upload;
	}
	public int getListNum() {
		return listNum;
	}
	public void setListNum(int listNum) {
		this.listNum = listNum;
	}
	public String getUrlFile() {
		return urlFile;
	}
	public void setUrlFile(String urlFile) {
		this.urlFile = urlFile;
	}
	
}
