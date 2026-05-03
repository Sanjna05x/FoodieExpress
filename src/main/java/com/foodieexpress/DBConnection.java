/* DBConnection.java - Simple MySQL Connection for Beginners
 * Location: src/main/java/com/foodieexpress/DBConnection.java
 * Usage: Connection con = DBConnection.getConnection();
 */

package com.foodieexpress;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // MySQL Database details - CHANGE PASSWORD to your MySQL root password
    private static final String URL = "jdbc:mysql://localhost:3306/food_ordering_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "5sanzz0#";  // UPDATE THIS TO YOUR MySQL PASSWORD
    
    // Method to get database connection
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ MySQL Database Connected Successfully!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed! Check URL/USER/PASSWORD");
            e.printStackTrace();
        }
        return connection;
    }
    
    // Method to close connection properly
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection");
                e.printStackTrace();
            }
        }
    }
}

