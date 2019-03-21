package com.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts.upload.FormFile;
public class FileManager {
	
	//upload
	
	//path : 파일을 저장할 경로
	//return : 서버에 저장할 새로운 파일 이름(saveFileName이 됨)
	public static String doFileUpload(FormFile uploadFile, String path) throws Exception{
		
		//매개변수로 받는 uploadFile은 originalFileName
		String newFileName = null;
		
		if(uploadFile==null){
			return null;
		}
		
		//클라이언트가 업로드한 파일이름
		String originalFileName = uploadFile.getFileName();
		
		//업로드파일 없을경우 반환값도 null
		if(originalFileName.equals("")){
			return null;
		}
		
		//확장자 분리
		//abc.txt 일 경우 substring를 통해서 .이후의 확장자에 해당하는 텍스트를 분리해옴
		String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		//확장자 없을경우 반환값도 null
		if(fileExt==null||fileExt.equals("")){
			return null;
		}
		
		//서버에 저장할 파일명 생성
		//매개변수가 원래 %의 갯수만큼 있어야 하는데 1$는 한번만 써도됨 
		//Y년 m월 d일 H시 M분 S초
		newFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", Calendar.getInstance());
		newFileName += System.nanoTime(); //10의 -9승
		newFileName += fileExt; //확장자 재반영
		
		File f = new File(path);
		
		//디렉토리 없을 경우 생성
		if(!f.exists())
			f.mkdirs();
		
		String fullFilePath = path + File.separator + newFileName;
		
		//파일 업로드(이 네줄만 있으면 formFile에서 알아서 올려준다.)
		byte[] fileData = uploadFile.getFileData();
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		fos.write(fileData);
		fos.close();
		
		return newFileName;	
	}
	
	//파일 삭제
	public static void doFileDelete(String fileName, String path){
		//파일 경로 받기
		String fullFilePath = path +  File.separator + fileName;
		File f = new File(fullFilePath);
		
		//파일삭제
		if(f.exists())
			f.delete();
	}
	
	//파일 다운로드
	//saveFileName : 서버에 저장된 파일명
	//originalFileName : 클라이언트가 업로드한 파일명
	//path : 서버에 저장된 경로
	public static boolean doFileDownload(String saveFileName, 
			String originalFileName, String path, HttpServletResponse response){
		
		String fullFilePath = path + File.separator + saveFileName;
		
		try {
			if(originalFileName==null||originalFileName.equals("")){
				originalFileName = saveFileName;
			}
			
			//파일이름이 한글일경우를 위해 작성
			//ISO-8859-1 에서 ISO를 생략하게되면 8859_1로 기재
			originalFileName = new String(originalFileName.getBytes("euc-kr"),"8859_1");
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
				
		try {
			File f = new File(fullFilePath);
			
			if(f.exists()){			
				byte readByte[] = new byte[4096];
				//octet-stream은 unknown..  
				response.setContentType("application/octet-stream");
				response.setHeader("Content-disposition", "attachement;fileName="+ originalFileName);
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
				OutputStream os = response.getOutputStream();
				
				int read;
				while((read=fis.read(readByte,0,4096))!=-1){
					//더이상 읽을 데이터가 없을때까지
					os.write(readByte,0,read);
				}
				os.flush();
				os.close();
				fis.close();
				return true;

			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return false;
	}
	
	
}
