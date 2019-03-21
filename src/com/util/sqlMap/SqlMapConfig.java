package com.util.sqlMap;

import java.io.IOException;
import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class SqlMapConfig {
	
	//final�����ε� �ʱ�ȭ ������ ����. 
	private static final SqlMapClient sqlMap;
		
	static{//static�̹Ƿ� �̹� �޸𸮻� �ö�����
		
		try {
			//�ش���ġ�� �ִ� xml�� �о sqlMap�� �Ҵ�
			String resource = "com/util/sqlMap/sqlMapConfig.xml";
			Reader reader = Resources.getResourceAsReader(resource);
			
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error initialixing class:"+ e);
		}
	}
	
	public static SqlMapClient getSqlMapInstance() {
		//�� �޼ҵ带 ȣ���ϸ� �޸𸮻� �ö� �ִ� sqlMap ��ȯ
		return sqlMap;
	}
}
