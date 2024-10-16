package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/omok"; // DB URL
    private static final String USER = "root"; // DB 사용자 이름
    private static final String PASSWORD = "1234"; // DB 비밀번호

    public static String getNicknameFromDB(String id) {
        String nickname = null;
        String query = "SELECT nickname FROM user WHERE id = ?"; // SQL 쿼리 수정

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                nickname = resultSet.getString("nickname");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nickname;
    }

    public static String getPasswordFromDB(String id) {
        String password = null;
        String query = "SELECT password FROM user WHERE id = ?"; // SQL 쿼리 수정

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                password = resultSet.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return password; // 조회된 비밀번호 반환
    }
}
