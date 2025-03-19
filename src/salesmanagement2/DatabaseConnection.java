package salesmanagement2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public Connection getConnection() {
        try {
            // JDBC URL, username, and password of MySQL server
            String URL = "jdbc:mysql://localhost:3306/salesmanagementsystem?characterEncoding=UTF-8";
            String USER = "root";
            String PASSWORD = "Mandicky003"; // Update the password here

            // Establishing a connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
