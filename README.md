# 🍽️ FoodieExpress — Food Ordering System

> 2nd Year | 4th Semester | Java + DBMS College Project

---

## 👩‍💻 Team Members

| Name | Roll Number |
|------|------------|
| Sanjna | 2420834 |
| Sanchi | 2420831 |
| Payal | 2420795 |
| Nishika | 2420785 |

---

## 💡 About

FoodieExpress is a web-based food ordering system developed as part of our 2nd Year 4th Semester college project. This project combines two of our core subjects — **Java Programming** and **Database Management System (DBMS)** — into one real-world application.

### Why we made this project?

In today's world, online food ordering has become a part of daily life. Apps like Swiggy and Zomato have made it very easy for people to order food from home. We were inspired by this idea and wanted to build something similar using the technologies we learned in class.

Through this project we wanted to:
- Apply **Java Servlet** concepts learned in class to a real project
- Practice **MySQL database design** with proper tables and relationships
- Understand how a **frontend and backend work together**
- Learn how to build and deploy a complete web application
- Get hands-on experience with **Maven and Tomcat server**

FoodieExpress allows users to register, login, browse a food menu, search and filter items, add food to cart, apply discount coupons and place orders — just like a real food ordering app!

---

## 🛠️ Tech Stack

Java | Jakarta Servlet 6.0 | MySQL | JDBC | Maven | Tomcat 10 | HTML | CSS | JavaScript

---

## 📂 Project Structure
FoodieExpress/
├── pom.xml
├── database.sql
└── src/main/
├── java/com/foodieexpress/
│   ├── DBConnection.java
│   ├── UserServlet.java
│   ├── MenuServlet.java
│   └── OrderServlet.java
└── webapp/
├── index.html
├── login.html
├── register.html
├── menu.html
├── cart.html
├── order-status.html
├── style.css
└── script.js

---

## ▶️ How to Run

```bash
# Step 1 - Kill Java
taskkill /F /IM java.exe

# Step 2 - Delete target folder manually

# Step 3 - Build
mvn clean install

# Step 4 - Run
mvn cargo:run

# Step 5 - Open in browser
http://localhost:9090/FoodieExpress/login.html
```

---

## 🔑 Test Login

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@foodieexpress.com | admin123 |
| Customer | customer@foodieexpress.com | customer123 |

---

> 🍽️ Made with ❤️ by Team FoodieExpress |
