package com.parkinglot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {
    
    private static final String URL = "jdbc:mysql://localhost:3306/parking_lot_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "root@12A"; 
    private static Connection connection = null;
    public static Connection getConnection() {
        
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (DBConfig.class) { 
                    if (connection == null || connection.isClosed()) {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    }
                }
            }     
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL Driver not found. Add the JAR to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Could not connect to the database. Check credentials or URL.");
            e.printStackTrace();
        }
        return connection;
    }
}
