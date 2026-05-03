/* MenuServlet.java - Complete JSON API for FoodieExpress Menu Page
 * Package: com.foodieexpress 
 * URL: /MenuServlet (exact mapping as requested)
 * Features: Returns all menu_items as JSON array from MySQL database
 * Supports ?category=Starters&search=burger filtering
 * Jakarta Servlet 6.0 + Java 25 compatible
 * For beginner students - clear comments everywhere
 */

package com.foodieexpress;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/MenuServlet")
public class MenuServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Step 1: Set JSON response headers
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*"); // Allow AJAX from menu.html
        
        PrintWriter out = response.getWriter();
        Connection con = null;
        
        try {
            // Step 2: Get database connection
            con = DBConnection.getConnection();
            
            // Step 3: Get filter parameters from menu.html
            String category = request.getParameter("category"); // All, Starters, Main Course, etc.
            String search = request.getParameter("search");      // User search term
            
            // Step 4: Build dynamic SQL query
            StringBuilder sql = new StringBuilder("SELECT * FROM menu_items WHERE 1=1 ");
            List<String> params = new ArrayList<>();
            
            if (category != null && !category.trim().isEmpty() && !"All".equals(category)) {
                sql.append("AND category = ? ");
                params.add(category);
            }
            
            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND (LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)) ");
                String searchPattern = "%" + search.trim() + "%";
                params.add(searchPattern);
                params.add(searchPattern);
            }
            
            sql.append("ORDER BY category, name");
            
            // Step 5: Execute query
            PreparedStatement ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            
            // Step 6: Convert to JSON array
            String jsonArray = resultSetToJson(rs);
            out.write(jsonArray);
            
            System.out.println("✅ MenuServlet served " + jsonArray.length() + " chars");
            
        } catch (SQLException e) {
            response.setStatus(500);
            out.write("{\"error\":\"Database Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(con);
        }
    }
    
    // Helper: Convert database rows to JSON array
    private String resultSetToJson(ResultSet rs) throws SQLException {
        List<String> items = new ArrayList<>();
        
        while (rs.next()) {
            // Escape strings for JSON
            String name = escapeJson(rs.getString("name"));
            String category = escapeJson(rs.getString("category"));
            String desc = escapeJson(rs.getString("description"));
            
            String itemJson = String.format(
                "{\"id\":%d,\"name\":\"%s\",\"price\":%.2f,\"category\":\"%s\",\"description\":\"%s\"}",
                rs.getInt("id"), name, rs.getDouble("price"), category, desc
            );
            items.add(itemJson);
        }
        
        return "[" + String.join(",", items) + "]";
    }
    
    // Helper: Escape special characters for JSON
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}

