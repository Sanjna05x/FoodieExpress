/* OrderServlet.java - Complete Order Management (Fixed)
 * GET /order - Show order status from database
 * POST /order - Place order from cart, save to orders + order_items tables
 * Handles cart data from script.js form POST
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con = null;
        
        try {
            con = DBConnection.getConnection();
            
            // Fetch user's orders with item count
            String query = """
                SELECT o.*, u.name, 
                       (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.id) as item_count
                FROM orders o 
                JOIN users u ON o.user_id = u.id 
                WHERE o.user_id = ? 
                ORDER BY o.order_date DESC
                """;
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("    <meta charset='UTF-8'>");
            out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("    <title>My Orders - FoodieExpress</title>");
            out.println("    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700' rel='stylesheet'>");
            out.println("    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("    <link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("    <link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");
            
            // Navigation with cart count
            out.println("    <nav class='navbar navbar-expand-lg navbar-dark fixed-top'>");
            out.println("        <div class='container'>");
            out.println("            <a class='navbar-brand fw-bold fs-3' href='index.html'>");
            out.println("                <i class='fas fa-utensils me-2'></i>FoodieExpress");
            out.println("            </a>");
            out.println("            <div class='navbar-nav ms-auto'>");
            out.println("                <a class='nav-link' href='menu.html'>Menu</a>");
            out.println("                <a class='nav-link' href='cart.html'>Cart</a>");
            out.println("            </div>");
            out.println("        </div>");
            out.println("    </nav>");
            
            out.println("    <div class='container mt-5 pt-5'>");
            out.println("        <div class='text-center mb-5'>");
            out.println("            <i class='fas fa-clipboard-list fa-4x text-orange mb-3'></i>");
            out.println("            <h1 class='display-5 fw-bold mb-3'>My Orders</h1>");
            out.println("            <p class='lead text-muted'>Track your food orders</p>");
            out.println("        </div>");
            
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                int orderId = rs.getInt("id");
                double total = rs.getDouble("total_price");
                String status = rs.getString("status");
                Timestamp date = rs.getTimestamp("order_date");
                int itemCount = rs.getInt("item_count");
                
                out.println("        <div class='card mb-4 shadow-lg border-0'>");
                out.println("            <div class='card-header bg-gradient-orange text-white'>");
                out.println("                <div class='row align-items-center'>");
                out.println("                    <div class='col-md-4'>");
                out.println("                        <h5 class='mb-1 fw-bold'>Order #" + orderId + "</h5>");
                out.println("                        <small>Placed on " + date + "</small>");
                out.println("                    </div>");
                out.println("                    <div class='col-md-4'>");
                out.println("                        <span class='badge bg-light text-dark fs-6'>" + itemCount + " items</span>");
                out.println("                    </div>");
                out.println("                    <div class='col-md-4 text-md-end'>");
                out.println("                        <span class='h5 fw-bold'>₹" + String.format("%.2f", total) + "</span>");
                out.println("                    </div>");
                out.println("                </div>");
                out.println("            </div>");
                out.println("            <div class='card-body'>");
                
                // Status badge
                String badgeClass = "placed".equals(status) ? "bg-warning" : 
                                  "preparing".equals(status) ? "bg-info" : 
                                  "delivered".equals(status) ? "bg-success" : "bg-secondary";
                out.println("                <span class='badge " + badgeClass + " fs-6 px-3 py-2'>" + status.toUpperCase() + "</span>");
                out.println("            </div>");
                out.println("        </div>");
            }
            
            if (!hasOrders) {
                out.println("        <div class='text-center py-5'>");
                out.println("            <i class='fas fa-shopping-bag fa-5x text-muted mb-4'></i>");
                out.println("            <h3 class='text-muted mb-3'>No orders yet</h3>");
                out.println("            <p class='text-muted'>Start ordering from our delicious menu</p>");
                out.println("            <a href='menu.html' class='btn btn-orange btn-lg px-5'>");
                out.println("                <i class='fas fa-utensils me-2'></i>Order Food");
                out.println("            </a>");
                out.println("        </div>");
            }
            
            out.println("    </div>");
            out.println("    <script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js'></script>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database Error");
        } finally {
            DBConnection.closeConnection(con);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con = null;
        
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            
            double totalPrice = 0;
            List<CartItem> cartItems = new ArrayList<>();
            
            // Parse all cart items from form data
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName.startsWith("item_")) {
                    try {
                        String jsonStr = request.getParameter(paramName);
                        CartItem item = parseCartItem(jsonStr);
                        if (item != null) {
                            cartItems.add(item);
                            totalPrice += item.price * item.quantity;
                        }
                    } catch (Exception e) {
                        // Skip invalid items
                    }
                }
            }
            
            if (cartItems.isEmpty()) {
                out.println("<div class='alert alert-danger'>No items in cart!</div>");
                return;
            }
            
            // Insert main order
            String orderQuery = "INSERT INTO orders (user_id, total_price, status) VALUES (?, ?, 'placed')";
            PreparedStatement orderPs = con.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userId);
            orderPs.setDouble(2, totalPrice);
            orderPs.executeUpdate();
            
            ResultSet generatedKeys = orderPs.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }
            
            // Insert order items
            String itemQuery = "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)";
            PreparedStatement itemPs = con.prepareStatement(itemQuery);
            for (CartItem item : cartItems) {
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, item.id);
                itemPs.setInt(3, item.quantity);
                itemPs.addBatch();
            }
            itemPs.executeBatch();
            
            con.commit();
            
            // Success page
            out.println("<!DOCTYPE html><html><head>");
            out.println("<title>Order Placed Successfully</title>");
            out.println("<meta http-equiv='refresh' content='3;url=order-status.html'>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("</head><body class='bg-light'><div class='container mt-5 text-center'>");
            out.println("<div class='alert alert-success display-4 mb-4'><i class='fas fa-check-circle'></i></div>");
            out.println("<h2>✅ Order #" + orderId + " placed successfully!</h2>");
            out.println("<h4>Total: ₹" + String.format("%.2f", totalPrice) + "</h4>");
            out.println("<p>Redirecting to orders in 3 seconds...</p>");
            out.println("<a href='order-status.html' class='btn btn-success btn-lg mt-3'>View Orders</a>");
            out.println("<a href='menu.html' class='btn btn-primary btn-lg mt-3'>Order More</a>");
            out.println("</div></body></html>");
            
        } catch (Exception e) {
            try { con.rollback(); } catch (SQLException ex) { }
            e.printStackTrace();
            out.println("<div class='alert alert-danger'>Order failed: " + e.getMessage() + "</div>");
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { }
            DBConnection.closeConnection(con);
        }
    }
    
    // Simple CartItem class
    private static class CartItem {
        int id;
        String name;
        double price;
        int quantity;
    }
    
    // Parse JSON string to CartItem
    private CartItem parseCartItem(String jsonStr) {
        try {
            // Simple JSON parser for {"id":1,"price":89,"quantity":1}
            jsonStr = jsonStr.trim();
            String idStr = extractJsonValue(jsonStr, "id");
            String priceStr = extractJsonValue(jsonStr, "price");
            String qtyStr = extractJsonValue(jsonStr, "quantity");
            
            CartItem item = new CartItem();
            item.id = Integer.parseInt(idStr);
            item.price = Double.parseDouble(priceStr);
            item.quantity = Integer.parseInt(qtyStr);
            return item;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Extract value from simple JSON like {"key":"value"}
    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "0";
        start += search.length();
        start = json.indexOf("\"", start) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}

