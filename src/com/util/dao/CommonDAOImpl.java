package com.util.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.util.sqlMap.SqlMapConfig;

public class CommonDAOImpl implements CommonDAO {
	
	private SqlMapClient sqlMap;
	
	public CommonDAOImpl() {
		//�����ڿ� ���� ��ü �ʱ�ȭ �� �� �ֵ���
		this.sqlMap = SqlMapConfig.getSqlMapInstance();
	}
	
	public static CommonDAO getInstance(){
		return new CommonDAOImpl();
	}
	
	@Override
	public void insertData(String id, Object value) throws SQLException {
		try {
			sqlMap.startTransaction(); //Ʈ����� ����
			
			sqlMap.insert(id,value); //insert�� ����
			
			sqlMap.commitTransaction(); //Ʈ����� ����
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();//������ �߻��ص� Ʈ������� �����Ű�� �ۼ�
		}
	}

	@Override
	public int updateData(String id, Object value) throws SQLException {
		int result = 0;
		try {
			sqlMap.startTransaction(); //Ʈ����� ����
			
			result = sqlMap.update(id,value); //update�� ����
			
			sqlMap.commitTransaction(); //Ʈ����� ����
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();//������ �߻��ص� Ʈ������� �����Ű�� �ۼ�
		}
		return result;
	}

	@Override
	public int updateData(String id, Map<String, Object> map)
			throws SQLException {
		int result = 0;
		try {
			sqlMap.startTransaction(); //Ʈ����� ����
			
			result = sqlMap.update(id,map); //update�� ����
			
			sqlMap.commitTransaction(); //Ʈ����� ����
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();//������ �߻��ص� Ʈ������� �����Ű�� �ۼ�
		}
		return result;
	}

	@Override
	public int deleteData(String id) throws SQLException {
		int result = 0;
		try {
			sqlMap.startTransaction(); 
			
			result = sqlMap.delete(id);
			
			sqlMap.commitTransaction();
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();
		}
		return result;
	}

	@Override
	public int deleteData(String id, Object value) throws SQLException {
		int result = 0;
		try {
			sqlMap.startTransaction(); 
			
			result = sqlMap.delete(id,value);
			
			sqlMap.commitTransaction();
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();
		}
		return result;
	}

	@Override
	public int deleteData(String id, Map<String, Object> map)
			throws SQLException {
		int result = 0;
		try {
			sqlMap.startTransaction(); 
			
			result = sqlMap.delete(id,map);
			
			sqlMap.commitTransaction();
			
		} catch (SQLException e) {
			System.out.println(e.toString());
		} finally {
			sqlMap.endTransaction();
		}
		return result;
	}

	@Override
	public Object getReadData(String id) throws SQLException {
		try {
			//select���� transaction start,end�� �ʿ����� ����
			return sqlMap.queryForObject(id);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public Object getReadData(String id, Object value) throws SQLException {
		try {
			//select���� transaction start,end�� �ʿ����� ����
			return sqlMap.queryForObject(id,value);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public Object getReadData(String id, Map<String, Object> map)
			throws SQLException {
		try {
			//select���� transaction start,end�� �ʿ����� ����
			return sqlMap.queryForObject(id,map);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public int getIntValue(String id) throws SQLException {
		try {
			return ((Integer)sqlMap.queryForObject(id)).intValue();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}

	@Override
	public int getIntValue(String id, Object value) throws SQLException {
		try {
			return ((Integer)sqlMap.queryForObject(id,value)).intValue();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}

	@Override
	public int getIntValue(String id, Map<String, Object> map)
			throws SQLException {
		try {
			return ((Integer)sqlMap.queryForObject(id,map)).intValue();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getListData(String id) throws SQLException {
		try {
			return (List<Object>)sqlMap.queryForList(id);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getListData(String id, Object value)
			throws SQLException {
		try {
			return (List<Object>)sqlMap.queryForList(id,value);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getListData(String id, Map<String, Object> map)
			throws SQLException {
		try {
			return (List<Object>)sqlMap.queryForList(id,map);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
}
