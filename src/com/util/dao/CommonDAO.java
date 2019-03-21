package com.util.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CommonDAO {
	
	//������ �߰�
	public void insertData(String id, Object value) throws SQLException;
	
	//������ ����
	public int updateData(String id, Object value) throws SQLException;
	public int updateData(String id, Map<String, Object> map) throws SQLException;
	
	//�����ͻ���
	public int deleteData(String id) throws SQLException;
	public int deleteData(String id, Object value) throws SQLException;
	public int deleteData(String id, Map<String, Object> map) throws SQLException;
	
	//�ش� ���ڵ� ��������
	public Object getReadData(String id) throws SQLException;
	public Object getReadData(String id, Object value) throws SQLException;
	public Object getReadData(String id, Map<String, Object> map) throws SQLException;

	public int getIntValue(String id) throws SQLException;
	public int getIntValue(String id, Object value) throws SQLException;
	public int getIntValue(String id, Map<String, Object> map) throws SQLException;

	public List<Object> getListData(String id) throws SQLException;
	public List<Object> getListData(String id, Object value) throws SQLException;
	public List<Object> getListData(String id, Map<String, Object> map) throws SQLException;
	
}
