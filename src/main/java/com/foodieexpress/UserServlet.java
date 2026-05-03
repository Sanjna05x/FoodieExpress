/* UserServlet.java - Handles Login and Registration for Beginners
 * Location: src/main/java/com/foodieexpress/UserServlet.java
 * URLs: /user?action=login, /user?action=register
 * Features: User registration, login with session, MySQL connection
 */

package com.foodieexpress;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con = null;
        
        try {
            con = DBConnection.getConnection();
            String action = request.getParameter("action");
            
            if ("login".equals(action)) {
                // === LOGIN LOGIC ===
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                
                String query = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Login successful - create session
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("userName", rs.getString("name"));
                    
                    // Redirect to menu
                    response.sendRedirect("menu.html");
                    return;
                } else {
                    // Login failed
                    out.println("<!DOCTYPE html><html><head><title>Error</title>");
                    out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
                    out.println("</head><body class='bg-light'><div class='container mt-5'>");
                    out.println("<div class='alert alert-danger'>❌ Invalid email or password!</div>");
                    out.println("<a href='login.html' class='btn btn-primary'>Try Again</a>");
                    out.println("</div></body></html>");
                }
                
            } else if ("register".equals(action)) {
                // === REGISTRATION LOGIC ===
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                
                String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    out.println("<!DOCTYPE html><html><head><title>Success</title>");
                    out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
                    out.println("</head><body class='bg-light'><div class='container mt-5'>");
                    out.println("<div class='alert alert-success'>✅ Registration successful! <a href='login.html'>Login now</a></div>");
                    out.println("</div></body></html>");
                } else {
                    out.println("<!DOCTYPE html><html><head><title>Error</title>");
                    out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
                    out.println("</head><body class='bg-light'><div class='container mt-5'>");
                    out.println("<div class='alert alert-danger'>❌ Email already exists! <a href='login.html'>Login instead</a></div>");
                    out.println("</div></body></html>");
                }
            }
            
        } catch (SQLException e) {
            out.println("<div class='alert alert-danger'>Database error: " + e.getMessage() + "</div>");
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(con);
        }
    }
}

