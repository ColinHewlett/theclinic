/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package colinhewlettsolutions.client.model.non_entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {
    private static final String DB_URL = "jdbc:ucanaccess://c:/pms/repository/66.accdb";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a match is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
