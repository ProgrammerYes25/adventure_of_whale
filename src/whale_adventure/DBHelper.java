package whale_adventure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    public DBHelper() {
        String url = "jdbc:mysql://127.0.0.1/?useSSL=false&user=root&password=mirim";
        String userName = "root";
        String password = "mirim";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("드라이버 연결 성공!");
            Connection connection = DriverManager.getConnection(url, userName, password);
            Statement stmt = connection.createStatement();
            String createSql = "CREATE DATABASE IF NOT EXISTS `AdaventureOfWhaleDB`";
            stmt.executeUpdate(createSql);
            url="jdbc:mysql://localhost/AdaventureOfWhaleDB?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, userName, password);
            stmt = connection.createStatement();
            createSql = "CREATE TABLE IF NOT EXISTS user_scoer_tabel(user_name VARCHAR(50), user_scoer INT)";
            stmt.executeUpdate(createSql);
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.print(e);
        }
    }
}
