package utils;
import java.sql.*;
public class DBUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/test_db";
    private static final String USER = "test_user";
    private static final String PASSWORD = "test_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void create(String query) throws SQLException {
        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    public static ResultSet read(String query) throws SQLException {
        Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    public static void update(String query) throws SQLException {
        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }

    public static void delete(String query) throws SQLException {
        try (Connection connection = getConnection(); Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        }
    }
}
