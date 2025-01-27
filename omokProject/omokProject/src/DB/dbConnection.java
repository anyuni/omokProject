package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

	public class dbConnection {
	    private static final String URL = "jdbc:mysql://localhost:3307/omok?serverTimezone=Asia/Seoul";
	    private static final String USER = "root";
	    private static final String PASSWD = "1234";
	    
	    public Connection getConnection() {
	        Connection conn = null;
	        try {
	            // MySQL JDBC 드라이버 로드
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            conn = DriverManager.getConnection(URL, USER, PASSWD);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("DB 연결 오류: " + e.getMessage());
	        }
	        return conn;
	    }
	

	    public static void main(String[] args) {
	        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWD)) {
	            // 드라이버 로드 (JDBC 4.0 이상에서는 필요하지 않을 수도 있음)
	            Class.forName("com.mysql.cj.jdbc.Driver");

	            // 테이블 생성
	            String createTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
	                                    "name VARCHAR(10) NOT NULL, " +
	                                    "id VARCHAR(50) NOT NULL PRIMARY KEY, " +
	                                    "password VARCHAR() NOT NULL)";
	            try (Statement stmt = conn.createStatement()) {
	                stmt.execute(createTableSQL);
	                System.out.println("테이블 생성 성공!");
	            } catch (SQLException e) {
	                System.out.println("테이블 생성 실패: " + e.getMessage());
	            }

	            // 데이터 삽입
	            String insertSQL = "INSERT INTO user (name, id, password) VALUES (?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
	                pstmt.setString(1, "이정민1");
	                pstmt.setString(2, "오목1");
	                pstmt.setString(3, "1111");
	                pstmt.executeUpdate();
	                System.out.println("데이터 삽입 성공!");
	            } catch (SQLException e) {
	                System.out.println("데이터 삽입 실패: " + e.getMessage());
	            }
	        } catch (ClassNotFoundException e) {
	            System.out.println("JDBC 드라이버를 찾을 수 없습니다: " + e.getMessage());
	        } catch (SQLException e) {
	            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
	        }
	    }
	}