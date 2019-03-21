package com.util.sqlMap;

import java.io.IOException;
import java.io.Reader;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class SqlMapConfig {
	
	//final변수인데 초기화 없으면 오류. 
	private static final SqlMapClient sqlMap;
		
	static{//static이므로 이미 메모리상에 올라가있음
		
		try {
			//해당위치에 있는 xml을 읽어서 sqlMap에 할당
			String resource = "com/util/sqlMap/sqlMapConfig.xml";
			Reader reader = Resources.getResourceAsReader(resource);
			
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error initialixing class:"+ e);
		}
	}
	
	public static SqlMapClient getSqlMapInstance() {
		//이 메소드를 호출하면 메모리상에 올라가 있는 sqlMap 반환
		return sqlMap;
	}
}
