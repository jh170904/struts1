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
	
	//path : ������ ������ ���
	//return : ������ ������ ���ο� ���� �̸�(saveFileName�� ��)
	public static String doFileUpload(FormFile uploadFile, String path) throws Exception{
		
		//�Ű������� �޴� uploadFile�� originalFileName
		String newFileName = null;
		
		if(uploadFile==null){
			return null;
		}
		
		//Ŭ���̾�Ʈ�� ���ε��� �����̸�
		String originalFileName = uploadFile.getFileName();
		
		//���ε����� ������� ��ȯ���� null
		if(originalFileName.equals("")){
			return null;
		}
		
		//Ȯ���� �и�
		//abc.txt �� ��� substring�� ���ؼ� .������ Ȯ���ڿ� �ش��ϴ� �ؽ�Ʈ�� �и��ؿ�
		String fileExt = originalFileName.substring(originalFileName.lastIndexOf("."));
		
		//Ȯ���� ������� ��ȯ���� null
		if(fileExt==null||fileExt.equals("")){
			return null;
		}
		
		//������ ������ ���ϸ� ����
		//�Ű������� ���� %�� ������ŭ �־�� �ϴµ� 1$�� �ѹ��� �ᵵ�� 
		//Y�� m�� d�� H�� M�� S��
		newFileName = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", Calendar.getInstance());
		newFileName += System.nanoTime(); //10�� -9��
		newFileName += fileExt; //Ȯ���� ��ݿ�
		
		File f = new File(path);
		
		//���丮 ���� ��� ����
		if(!f.exists())
			f.mkdirs();
		
		String fullFilePath = path + File.separator + newFileName;
		
		//���� ���ε�(�� ���ٸ� ������ formFile���� �˾Ƽ� �÷��ش�.)
		byte[] fileData = uploadFile.getFileData();
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		fos.write(fileData);
		fos.close();
		
		return newFileName;	
	}
	
	//���� ����
	public static void doFileDelete(String fileName, String path){
		//���� ��� �ޱ�
		String fullFilePath = path +  File.separator + fileName;
		File f = new File(fullFilePath);
		
		//���ϻ���
		if(f.exists())
			f.delete();
	}
	
	//���� �ٿ�ε�
	//saveFileName : ������ ����� ���ϸ�
	//originalFileName : Ŭ���̾�Ʈ�� ���ε��� ���ϸ�
	//path : ������ ����� ���
	public static boolean doFileDownload(String saveFileName, 
			String originalFileName, String path, HttpServletResponse response){
		
		String fullFilePath = path + File.separator + saveFileName;
		
		try {
			if(originalFileName==null||originalFileName.equals("")){
				originalFileName = saveFileName;
			}
			
			//�����̸��� �ѱ��ϰ�츦 ���� �ۼ�
			//ISO-8859-1 ���� ISO�� �����ϰԵǸ� 8859_1�� ����
			originalFileName = new String(originalFileName.getBytes("euc-kr"),"8859_1");
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
				
		try {
			File f = new File(fullFilePath);
			
			if(f.exists()){			
				byte readByte[] = new byte[4096];
				//octet-stream�� unknown..  
				response.setContentType("application/octet-stream");
				response.setHeader("Content-disposition", "attachement;fileName="+ originalFileName);
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
				OutputStream os = response.getOutputStream();
				
				int read;
				while((read=fis.read(readByte,0,4096))!=-1){
					//���̻� ���� �����Ͱ� ����������
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
