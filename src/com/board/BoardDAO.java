package com.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
	
	private Connection conn;
	public BoardDAO(Connection conn){
		this.conn = conn;
	}
	
	//게시글 카운팅
	public int getMaxNum(){
		int maxNum = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql ; 
		try {
			
			sql = "select nvl(max(num),0) from board";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()){
				maxNum = rs.getInt(1);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return maxNum;
	}
	
	//게시글 database 입력
	public int insertData(BoardForm dto){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql; 
		try {
			sql = "insert into board (num,name,pwd,email,subject,content,";
			sql += "ipAddr,hitCount,created) values (?,?,?,?,?,?,?,0,sysdate)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getName());
			pstmt.setString(3, dto.getPwd());
			pstmt.setString(4, dto.getEmail());
			pstmt.setString(5, dto.getSubject());
			pstmt.setString(6, dto.getContent());
			pstmt.setString(7, dto.getIpAddr());
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	//게시글 갯수 카운팅
	public int getDataCount(String searchKey, String searchValue){
		
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			searchValue = "%" + searchValue +"%" ;
			sql = "select nvl(count(*),0) from board ";
			sql += "where " + searchKey + " like ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, searchValue);
			rs = pstmt.executeQuery();
			if(rs.next())
				result = rs.getInt(1);
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	//리스트 출력
	public List<BoardForm> getLists(int start, int end, String searchKey, String searchValue){
		
		List<BoardForm> lists = new ArrayList<BoardForm>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			searchValue = "%" + searchValue + "%";
			
			sql = "select * from (";
			sql += "select rownum rnum, data.* from (";
			sql += "select num,name,subject,hitCount,";
			sql += "to_char(created,'YYYY-MM-DD') created ";
			sql += "from board where " + searchKey + " like ? ";
			sql += "order by num desc) data) ";
			sql += "where rnum>=? and rnum<=? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				BoardForm dto = new BoardForm();
				dto.setNum(rs.getInt("num"));	
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				
				lists.add(dto);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return lists;
	}
	
	//조회수 증가
	public int updateHitCount(int num){
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		try {
			sql = "update board set hitCount=hitCount+1 where num=? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	//게시글 조회
	public BoardForm getReadData(int num){
		BoardForm dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select num,name,pwd,email,subject,content,ipAddr,hitCount,";
			sql += "created from board where num=? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				dto = new BoardForm();
				
				dto.setNum(rs.getInt("num"));	
				dto.setName(rs.getString("name"));
				dto.setPwd(rs.getString("pwd"));
				dto.setEmail(rs.getString("email"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setIpAddr(rs.getString("ipAddr"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
			}
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return dto;		
	}
	
	//게시글 수정
	public int updateData(BoardForm dto){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql; 
		try {
			sql = "update board set name=?,pwd=?,email=?,subject=?,content=? ";
			sql += "where num=?";
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getName());
			pstmt.setString(2, dto.getPwd());
			pstmt.setString(3, dto.getEmail());
			pstmt.setString(4, dto.getSubject());
			pstmt.setString(5, dto.getContent());
			pstmt.setInt(6, dto.getNum());
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	
	//게시글 삭제
	public int deleteData(int num){
		
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "delete board where num=? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
}
