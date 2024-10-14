package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3307/omok?serverTimezone=Asia/Seoul";
    private static final String USER = "root";
    private static final String PASSWD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWD);
    }

    public static boolean login(String id, String password) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWD)) {
            String query = "SELECT * FROM user WHERE id = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, id);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();  // 로그인 성공 여부 반환
                }
            }
        } catch (SQLException e) {
            System.out.println("로그인 실패: " + e.getMessage());
            return false;
        }
    }
    
    private static Connection conn = null;

    // 데이터베이스 연결을 설정하는 메서드
    public static void connect() {
        try {
            // JDBC 드라이버를 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            conn = DriverManager.getConnection(URL, USER, PASSWD);
            System.out.println("Database connected!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
        }
    }

    // 회원가입 메서드
 // 회원가입 메서드
    public static boolean insertUser(String name, String id, String password, String hp_prefix,
                                      String hp_mid, String hp_end, String email, String gender, 
                                      java.sql.Date birthDate, String postalCode, 
                                      String roadName, String addressDetail, byte[] imageBytes, String nickname) {
        if (conn == null) {
            connect();  // 연결이 없으면 연결 시도
        }

        String sql = "INSERT INTO user (id, password, name, hp_prefix, hp_mid, hp_end, frontemail, backemail, sex, birth, postaladdress, roadnameaddress, detailladdress, profile, nickname) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, hp_prefix); // hp_prefix
            pstmt.setString(5, hp_mid); // hp_mid
            pstmt.setString(6, hp_end); // hp_end
            pstmt.setString(7, email.split("@")[0]); // frontemail
            pstmt.setString(8, email.split("@")[1]); // backemail
            pstmt.setString(9, gender);
            pstmt.setDate(10, birthDate);
            pstmt.setString(11, postalCode);
            pstmt.setString(12, roadName);
            pstmt.setString(13, addressDetail);
            pstmt.setString(15, nickname);
            if (imageBytes != null) {
                pstmt.setBytes(14, imageBytes); // 이미지 바이트 배열을 DB에 저장
            } else {
                pstmt.setNull(14, java.sql.Types.BLOB); // 이미지가 없는 경우
            }
            int result = pstmt.executeUpdate();
            return result > 0; // 성공적으로 삽입 시 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 삽입 실패 시 false 반환
        }
    }

}
